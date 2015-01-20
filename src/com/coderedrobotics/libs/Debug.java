package com.coderedrobotics.libs;

import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;

/**
 *
 * @author Michael Spoehr
 */
public final class Debug {

    public static boolean debugMode = true;
    private static int debugLevel = 5;
    private static boolean errorsDisplayed;
    public static int CRITICAL = 1;
    public static int WARNING = 2;
    public static int STANDARD = 3;
    public static int EXTENDED = 4;
    public static int EVERYTHING = 5;
    public static int DISABLEPRINT = 6;

    private static final DriverStationLCD lcd = DriverStationLCD.getInstance();
    // Note: if debug level for incomming print is set to 0, it will override
    // the debug level, even if it is set to 6.

    public static void setDebugLevel(int level) {
        debugLevel = level;
    }

    public static int getDebugLevel() {
        return debugLevel;
    }

    public static boolean isErrorsDisplayed() {
        return errorsDisplayed;
    }

    public static void setErrorsDisplayed(boolean displayErrors) {
        errorsDisplayed = displayErrors;
    }

    public static void disableDebugPrinting() {
        debugLevel = 6;
    }

    public static void print(Object object, int level) {
        if (level <= debugLevel && debugLevel != 6) {
            System.out.print(object);
        } else if (level == 0) {
            System.out.print(object);
        }
    }

    public static void println(Object object, int level) {
        if (level <= debugLevel && debugLevel != 6) {
            System.out.println(object);
        } else if (level == 0) {
            System.out.println(object);
        }
    }

    public static void print(Object object) {
        int level = 0;
        if (level <= debugLevel && debugLevel != 6) {
            System.out.print(object);
        } else if (level == 0) {
            System.out.print(object);
        }
    }

    public static void println(Object object) {
        int level = 0;
        if (level <= debugLevel && debugLevel != 6) {
            System.out.println(object);
        } else if (level == 0) {
            System.out.println(object);
        }
    }

    public static void printErr(Exception e) {
        if (errorsDisplayed) {
            System.err.println(e);
        }
    }

    public static void forcePrintErr(Exception e) {
        System.err.println(e);
    }

    public static void printDriverStationMessage(int line, int column, String text) {
        Line lineline;
        switch (line) {
            case 1:
                lineline = DriverStationLCD.Line.kUser1;
                break;
            case 2:
                lineline = DriverStationLCD.Line.kUser2;
                break;
            case 3:
                lineline = DriverStationLCD.Line.kUser3;
                break;
            case 4:
                lineline = DriverStationLCD.Line.kUser4;
                break;
            case 5:
                lineline = DriverStationLCD.Line.kUser5;
                break;
            case 6:
                lineline = DriverStationLCD.Line.kUser6;
                break;
            default:
                printErr(new Exception("That line doesn't exist."));
                return;
        }
        lcd.println(lineline, column, text);
        lcd.updateLCD();
    }

    public static void clearDriverStationMessages() {
        lcd.clear();
        lcd.updateLCD();
    }
}