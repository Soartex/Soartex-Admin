package net.soartex.admin;

import java.io.File;

import java.lang.reflect.Field;

class Strings {
	// TODO: Important Strings

	static final String SOARTEX_ADMIN = "Soartex Admin";

	static final String OS = System.getProperty("os.name").toUpperCase();

	static final String ICON_NAME = "icon.ico";

	static String MODDED_URL = "";

	static final String MOD_CSV = "mods.csv";
	static final String TECHNIC_LIST = "technic.txt";
	static final String FTB_LIST = "ftb.txt";

	static final String COMMA = ",";
	static final String SPACE = " ";
	static final String UNDERSCORE = "_";

	static final String DATE_FORMAT = "MM/dd/yyyy";

	static final String TEMPORARY_DATA_LOCATION_A = getTMP() + File.separator + ".Soartex_Launcher_A";
	static final String TEMPORARY_DATA_LOCATION_B = getTMP() + File.separator + ".Soartex_Launcher_B";

	static final String ZIP_FILES_EXT = "*.zip";
	static final String CSV_FILES_EXT = "*.csv";

	// TODO: Preferences Keys

	static final String PREF_X = "x";
	static final String PREF_Y = "y";

	static final String PREF_WIDTH = "width";
	static final String PREF_HEIGHT = "height";

	static final String PREF_MAX = "maximized";

	//TODO: other strings

	public static final String VALID_BUTTON = "Test Validness";
	public static final String UPDATESIZE_BUTTON = "Update File Size";
	public static final String UPDATEDATE_BUTTON = "Update Date Modifed";
	public static final String NEWROW_BUTTON = "Add New Row";
	public static final String DELETEROW_BUTTON = "Delete Selected Rows";

	public static final String NAME_COLUMN = "Name";
	public static final String VERSION_COLUMN = "Version";
	public static final String GAMEVERSION_COLUMN = "Game Version";
	public static final String SIZE_COLUMN = "Size";
	public static final String MODIFIED_COLUMN = "Updated";

	public static final String UPDATE_BUTTON = "Update!";
	public static final String SAVE_BUTTON = "Save a Copy";

	// TODO: Methods

	
	public static void modUrl(String u){
		MODDED_URL=u;
	}
	
	private static String getTMP () {

		if (OS.contains("WIN")) return System.getenv("TMP");

		else if (OS.contains("MAC") || OS.contains("DARWIN")) return System.getProperty("user.home") + "/Library/Caches/";
		else if (OS.contains("NUX")) return System.getProperty("user.home");

		return System.getProperty("user.dir");

	}

	static String getMinecraftDir () {

		if (OS.contains("WIN")) return System.getenv("APPDATA") + "\\.minecraft";

		else if (OS.contains("MAC") || OS.contains("DARWIN")) return System.getProperty("user.home") + "/Library/Application Support/minecraft";
		else if (OS.contains("NUX")) return System.getProperty("user.home");

		return System.getProperty("user.dir");

	}	
}