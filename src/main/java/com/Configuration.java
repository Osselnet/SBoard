/*
    Copyright 2016-2019 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com;

import jfork.nproperty.Cfg;
import jfork.nproperty.ConfigParser;
import lombok.Getter;

@Getter
public class Configuration {

    @Cfg("VERSION")
    private int version;

    @Cfg("FILE")
    private String filename = "test.gcode";

    @Cfg("CONTROLLER_FIRMWARE")
    private String controller = "GRBL";

    @Cfg("PORT")
    private String port = "/dev/ttyUSB0";

    @Cfg("BAUD")
    private String baudrate = "115200";

    @Cfg("HOME")
    private Boolean home = false;

    @Cfg("LIST_PORTS")
    private Boolean list = false;

    @Cfg("PRINT_STREAM")
    private Boolean print_stream = false;

    @Cfg("PRINT_PROGRESSBAR")
    private Boolean print_progressbar = false;

    @Cfg("RESET_ALARM")
    private Boolean reset_alarm = false;

    @Cfg("DAEMON")
    private Boolean daemon = false;

    @Cfg("WORKSPACE")
    private String workspace = "";

    @Cfg("DRIVER")
    private String driver = "";

    public Configuration() {
        try {
            ConfigParser.parse(this, "./src/main/resources/config.ini");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * Returns true if the given option enum was entered as a command line argument
//     *
//     * @param option the option to check for
//     * @return true if it was given
//     */
//    public boolean hasOption(OptionEnum option) {
//        return commandLine.hasOption(option.getOptionName());
//    }
//
//    /**
//     * Retuns the options extra argument value if present
//     *
//     * @param option the option value to fetch
//     * @return the value as string if found, otherwise null is returned
//     */
//    public String getOptionValue(OptionEnum option) {
//        return commandLine.getOptionValue(option.getOptionName());
//    }
//
//    /**
//     * Return all configured options
//     *
//     * @return all options
//     */
//    public Options getOptions() {
//        return options;
//    }
}
