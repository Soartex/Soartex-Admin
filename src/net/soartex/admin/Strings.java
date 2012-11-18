package net.soartex.admin;

import java.io.File;

import java.lang.reflect.Field;

class Strings {
	// TODO: Important Strings

	static final String SOARTEX_ADMIN = "Soartex Admin";

	static final String OS = System.getProperty("os.name").toUpperCase();

	static final String ICON_NAME = "icon.ico";

	static final String MODDED_URL = "http://soartex.net/modded/";

	static final String MOD_CSV = "mods.csv";
	static final String TECHNIC_LIST = "technic.txt";
	static final String FTB_LIST = "ftb.txt";

	static final String COMMA = ",";
	static final String SPACE = " ";
	static final String UNDERSCORE = "_";

	static final String BYTES = " bytes";
	static final String KILOBYTES = " kilobytes";
	static final String MEGABYTES = " megabytes";

	static final String DATE_FORMAT = "MM/dd/yyyy";

	static final String TEMPORARY_DATA_LOCATION_A = getTMP() + File.separator + ".Soartex_Launcher_A";
	static final String TEMPORARY_DATA_LOCATION_B = getTMP() + File.separator + ".Soartex_Launcher_B";

	static final String ZIP_FILES_EXT = "*.zip";

	// TODO: Preferences Keys

	static final String PREF_X = "x";
	static final String PREF_Y = "y";

	static final String PREF_WIDTH = "width";
	static final String PREF_HEIGHT = "height";

	static final String PREF_MAX = "maximized";

	//TODO: other strings

	public static final String TECHNIC_BUTTON = "Technic";
	public static final String FTB_BUTTON = "FTB (WIP!)";
	public static final String ALL_BUTTON = "Select All";
	public static final String NONE_BUTTON = "Select None";

	public static final String NAME_COLUMN = "Name";
	public static final String VERSION_COLUMN = "Version";
	public static final String GAMEVERSION_COLUMN = "Game Version";
	public static final String SIZE_COLUMN = "Size";
	public static final String MODIFIED_COLUMN = "Updated";

	public static final String BROWSE_BUTTON = "Browse";

	public static final String ZIP_FILES = "Texture Packs (*.zip)";

	public static final String PATCH_BUTTON = "Update!";

	public static final String LANGUAGE_ITEM = "Language";

	public static final String ENGLISH_ITEM = "English";
	public static final String FRENCH_ITEM = "French";
	public static final String SPANISH_ITEM = "Spanish";
	public static final String ITALIAN_ITEM = "Italian";
	public static final String GERMAN_ITEM = "German";
	public static final String HEBREW_ITEM = "Hebrew";
	public static final String ARABIC_ITEM = "Arabic";
	public static final String CHINESE_ITEM = "Chinese";
	public static final String JAPANESE_ITEM = "Japanese";

	public static final String HELP_ITEM = "Help";

	// TODO: Methods

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