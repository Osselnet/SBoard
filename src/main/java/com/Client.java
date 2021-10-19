package com;

import com.willwinder.universalgcodesender.connection.ConnectionDriver;
import com.willwinder.universalgcodesender.connection.ConnectionFactory;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.pendantui.PendantUI;
import com.willwinder.universalgcodesender.utils.Settings;
import com.willwinder.universalgcodesender.utils.SettingsFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Client {
    private final Configuration configuration;
    private BackendAPI backend;
    private PendantUI pendantUI;

    public static void main(String[] args) {

        Configuration configuration = new Configuration();
        Client client = new Client(configuration);
        client.runJob();
    }

    public Client(Configuration configuration) {
        this.configuration = configuration;
    }

    public void runJob() {
        try {
            if (!configuration.getWorkspace().isEmpty()) {
                String directory = configuration.getWorkspace();
                setWorkspaceDirectory(directory);
            }

            if (!configuration.getDriver().isEmpty()) {
                ConnectionDriver driver = ConnectionDriver.valueOf(configuration.getDriver());
                setConnectionDriver(driver);
            }

            if (configuration.getList()) {
                listPorts();
                System.exit(0);
            }

            initializeBackend();

            if (configuration.getDaemon()) {
                startDaemon();
            }

            if (configuration.getReset_alarm()) {
                resetAlarm();
            }

            if (configuration.getHome()) {
                homeMachine();
            }

            if (!configuration.getFilename().isEmpty()) {
                sendFile();
            }

            while (pendantUI != null) {
                Thread.sleep(100);
            }

            backend.disconnect();
        } catch (Exception e) {
            // TODO add fancy error handling
            e.printStackTrace();
            System.exit(-1);
        } finally {
            // TODO This is a hack to exit threads, find out why threads aren't killed
            System.exit(0);
        }
    }

    private void setConnectionDriver(ConnectionDriver driver) {
        Settings settings = SettingsFactory.loadSettings();
        settings.setConnectionDriver(driver);
        SettingsFactory.saveSettings(settings);
    }

    private static void setWorkspaceDirectory(String directory) {
        Settings settings = SettingsFactory.loadSettings();
        settings.setWorkspaceDirectory(directory);
        SettingsFactory.saveSettings(settings);
    }

    private void startDaemon() {
        pendantUI = new PendantUI(backend);
        pendantUI.start();
    }

    /**
     * Resets an alarm in the controller
     */
    private void resetAlarm() {
        try {
            backend.killAlarmLock();
        } catch (Exception e) {
            throw new RuntimeException("The alarm couldn't be reset", e);
        }
    }

    /**
     * Lists all available ports
     */
    private void listPorts() {
        Settings settings = SettingsFactory.loadSettings();
        List<String> portNames = ConnectionFactory.getPortNames(settings.getConnectionDriver());
        System.out.println("Available ports: " + Arrays.toString(portNames.toArray()));
    }

    /**
     * Performs homing of the machine
     */
    private void homeMachine() {
        try {
            backend.performHomingCycle();
            while (!backend.isIdle()) {
                Thread.sleep(10);
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldn't home machine", e);
        }
    }

    /**
     * Prints a help message with all available properties for the program
     */
//    private void printHelpMessage() {
//        System.out.println(SOFTWARE_NAME + " " + Version.getVersionString());
//        System.out.println();
//        System.out.println(SOFTWARE_DESCRIPTION);
//        System.out.println();
//
//        HelpFormatter formatter = new HelpFormatter();
//        formatter.printHelp(SOFTWARE_NAME, configuration.getOptions());
//    }

    /**
     * Starts streaming a file to the controller
     */
    private void sendFile() {
        String filename = configuration.getFilename();
        if (StringUtils.isEmpty(filename)) {
            return;
        }

        try {
            System.out.println("Running file \"" + filename + "\"");
            File file = new File(filename);
            backend.setGcodeFile(file);

            if (!backend.canSend()) {
                System.out.println("The controller is in a state where it isn't able to process the file: " + backend.getControlState());
                return;
            }


            backend.send();

            while (backend.isSendingFile()) {
                Thread.sleep(50);
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldn't send file", e);
        }
    }

    /**
     * Initialize and connects the backend to the controller
     */
    private void initializeBackend() {
        backend = BackendInitializerHelper.getInstance().initialize(configuration);

        // It seems like the settings are working, save them for later.
        SettingsFactory.saveSettings();

        if (configuration.getPrint_stream()) {
            backend.addControllerListener(new ProcessedLinePrinter());
        } else if (configuration.getPrint_progressbar()) {
            ProgressBarPrinter progressBarPrinter = new ProgressBarPrinter(backend);
            backend.addControllerListener(progressBarPrinter);
            backend.addUGSEventListener(progressBarPrinter);
        }
    }

}
