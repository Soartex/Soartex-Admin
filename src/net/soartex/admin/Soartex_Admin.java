package net.soartex.admin;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import java.util.concurrent.TimeUnit;

import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.soartex.admin.console.TextAreaOutputStream;
import net.soartex.admin.helpers.FTPupload;
import net.soartex.admin.helpers.TableManger;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Soartex_Admin {

	// TODO: Program Variables

	private static final Preferences prefsnode = Preferences.userNodeForPackage(Soartex_Admin.class).node(Strings.SOARTEX_ADMIN);

	private static URL tabledata;

	private static HashMap<TableItem, String> moddatamap;

	private static String password;
	private static String username;
	private static String host;
	private static String tablePath;
	private static String tablePathURL;

	// TODO: SWT Components

	private static Display display;
	private static Shell shell;
	private static JFrame frame;

	private static Button checkValid;
	private static Button updateSize;
	private static Button updateDate;
	private static Button newRow;
	private static Button deleteRow;

	public static Table table;

	public static TableColumn name;
	public static TableColumn version;
	public static TableColumn gameversion;
	public static TableColumn size;
	public static TableColumn modified;

	public static Button update;
	public static Button save;

	private static ProgressBar progress;

	public static void main (final String[] args) {

		askInfo();

		initializeLogger();

		initializeShell();

		//new mod path
		String readline = null;
		try {
			URL data2 = new URL("http://soartex.net/patcher/data/"+"ziplocation.txt");
			final BufferedReader in2 = new BufferedReader(new InputStreamReader(data2.openStream()));
			readline = in2.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Strings.modUrl(readline);
		
		initializeComponents();

		startEventLoop();
	}

	private static void askInfo() {
		String readline2 = null;
		try {
			URL data2 = new URL("http://soartex.net/patcher/data/"+"tablelocation.txt");
			final BufferedReader in2 = new BufferedReader(new InputStreamReader(data2.openStream()));
			readline2 = in2.readLine();
			tablePathURL = readline2;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//host
		JTextField hostField = new JTextField();
		//username
		JTextField userField = new JTextField();
		//pasword
		JPasswordField passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		//path
		JTextField pathField = new JTextField();
		
		//System.out.println(readline2.indexOf("/",8)+"");
		readline2 = readline2.substring(readline2.indexOf("/",8));
		
		pathField.setText(readline2);
		//main textbox
		Object[] obj = {
				"Enter FTP Host:\n",hostField,"\n",
				"Enter FTP Username:\n",userField,"\n",
				"Please enter the password:\n", passwordField,"\n",
				"Enter .cvs save path:\n",pathField,"\n",};
		Object stringArray[] = {"OK"};
		JOptionPane.showOptionDialog(null, obj, "Enter Info",0, JOptionPane.PLAIN_MESSAGE, null, stringArray, obj);
		//host
		host = hostField.getText();
		host=host.replaceAll("@", "%40");
		//username
		username = userField.getText();
		username=username.replaceAll("@", "%40");
		//pasword
		password = new String(passwordField.getPassword());
		password=password.replaceAll("@", "%40");
		//path
		tablePath = pathField.getText();
	}

	private static void initializeLogger() {
		frame = new JFrame();        
		//size of console
		JTextArea ta = new JTextArea("", 10, 80);

		PrintStream ps = new PrintStream(new TextAreaOutputStream(ta));
		System.setOut(ps);
		System.setErr(ps);
		frame.add(new JScrollPane(ta));
		frame.setFocusableWindowState(false);
		frame.pack();
		frame.setTitle("Soartex Admin Console");
		frame.setVisible(true);
		frame.setFocusableWindowState(true);
	}

	private static void initializeShell () {
		display = Display.getDefault();

		shell = new Shell(display);

		shell.setText(Strings.SOARTEX_ADMIN);

		shell.setLocation(prefsnode.getInt(Strings.PREF_X, 100), prefsnode.getInt(Strings.PREF_Y, 100));
		shell.setSize(prefsnode.getInt(Strings.PREF_WIDTH, 500), prefsnode.getInt(Strings.PREF_HEIGHT, 300));

		if (prefsnode.getBoolean(Strings.PREF_MAX, false)) shell.setMaximized(true);

		shell.addListener(SWT.Close, new ExitListener());

		shell.setLayout(new GridLayout(5, true));

	}

	private static void initializeComponents () {

		// TODO: Mod Table
		GridData gd = new GridData();

		table = new Table(shell, SWT.BORDER|SWT.FULL_SELECTION|SWT.CHECK);
		name = new TableColumn(table, SWT.CENTER);
		version = new TableColumn(table, SWT.CENTER);
		gameversion = new TableColumn(table, SWT.CENTER);
		size = new TableColumn(table, SWT.CENTER);
		modified = new TableColumn(table, SWT.CENTER);

		new TableManger(table);

		name.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        // sort column 1
		        TableItem[] items = table.getItems();
		        Collator collator = Collator.getInstance(Locale.getDefault());
		        for (int i = 1; i < items.length; i++) {
		          String value1 = items[i].getText(0);
		          for (int j = 0; j < i; j++) {
		            String value2 = items[j].getText(0);
		            if (collator.compare(value1, value2) < 0) {
		              String[] values = { items[i].getText(0),
		                  items[i].getText(1) };
		              items[i].dispose();
		              TableItem item = new TableItem(table, SWT.NONE, j);
		              item.setText(values);
		              items = table.getItems();
		              break;
		            }
		          }
		        }
		      }
		    });
		
		name.setText(Strings.NAME_COLUMN);
		version.setText(Strings.VERSION_COLUMN);
		gameversion.setText(Strings.GAMEVERSION_COLUMN);
		size.setText(Strings.SIZE_COLUMN);
		modified.setText(Strings.MODIFIED_COLUMN);

		table.setHeaderVisible(true);

		loadTable();

		name.pack();
		gameversion.pack();
		version.pack();
		size.pack();
		modified.pack();

		gd = new GridData();
		gd.horizontalSpan = 5;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		table.setLayoutData(gd);

		// TODO: Selection Buttons
		gd = new GridData();

		checkValid = new Button(shell, SWT.PUSH);
		updateSize = new Button(shell, SWT.PUSH);
		updateDate = new Button(shell, SWT.PUSH);
		newRow = new Button(shell, SWT.PUSH);
		deleteRow = new Button(shell, SWT.PUSH);

		checkValid.setText(Strings.VALID_BUTTON);
		updateSize.setText(Strings.UPDATESIZE_BUTTON);
		updateDate.setText(Strings.UPDATEDATE_BUTTON);
		newRow.setText(Strings.NEWROW_BUTTON);
		deleteRow.setText(Strings.DELETEROW_BUTTON);

		final ButtonListener buttonListener = new ButtonListener();

		checkValid.addSelectionListener(buttonListener);
		updateSize.addSelectionListener(buttonListener);
		updateDate.addSelectionListener(buttonListener);
		newRow.addSelectionListener(buttonListener);
		deleteRow.addSelectionListener(buttonListener);

		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		checkValid.setLayoutData(gd);
		updateSize.setLayoutData(gd);
		updateDate.setLayoutData(gd);
		newRow.setLayoutData(gd);
		deleteRow.setLayoutData(gd);
		// TODO: Patch Button

		update = new Button(shell, SWT.PUSH);
		update.setText(Strings.UPDATE_BUTTON);
		update.addSelectionListener(new UpdateListener());

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		update.setLayoutData(gd);

		// TODO: Progress Bar

		progress = new ProgressBar(shell, SWT.NORMAL);

		progress.setLayoutData(gd);
		
		//TODO: save button
		save = new Button(shell, SWT.PUSH);
		save.setText(Strings.SAVE_BUTTON);
		save.addSelectionListener(buttonListener);
		
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = SWT.FILL;
		save.setLayoutData(gd);
	}

	private static void loadTable () {
		try{
			String readline = null;
			//iteminfo storage
			ArrayList<String[]> itemsInfo = new ArrayList<String[]>();
			tabledata = new URL(tablePathURL);
			moddatamap = new HashMap<>();
			final BufferedReader in = new BufferedReader(new InputStreamReader(tabledata.openStream()));
			readline = in.readLine();

			while (readline != null) {
				//add file info
				final String[] itemtext = new String[5];
				itemtext[0] = readline.split(Strings.COMMA)[0];

				//debug
				System.out.println("Loading: "+itemtext[0]);

				//modversion
				try{
					itemtext[1] = readline.split(Strings.COMMA)[1];
				} catch(final Exception e){
					itemtext[1] = "null";
				}

				//gameversion
				try{
					itemtext[2] = readline.split(Strings.COMMA)[2];
				} catch(final Exception e){
					itemtext[1] = "null";
				}

				//size
				try{
					itemtext[3] =readline.split(Strings.COMMA)[3];

				} catch(final Exception e){
					itemtext[1] = "null";
				}					

				//date modified
				try{
					itemtext[4] = readline.split(Strings.COMMA)[4];
				} catch(final Exception e){
					itemtext[4] = "null";
				}

				//save info
				itemsInfo.add(itemtext);
				readline = in.readLine();							
			}//end of while

			name.setWidth(100);
			gameversion.setWidth(50);
			version.setWidth(50);
			size.setWidth(50);
			modified.setWidth(75);
			for(int i=0; i<itemsInfo.size();i++){
				final TableItem item = new TableItem(table, SWT.NONE);
				item.setText(itemsInfo.get(i));
				moddatamap.put(item, null);
			}
			System.out.println("=======DONE=======");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void startEventLoop () {

		shell.open();

		while (!shell.isDisposed()) {

			if (!display.readAndDispatch()) display.sleep();

		}
		frame.dispose();
	}

	// TODO: Listeners

	private static final class ButtonListener implements SelectionListener {

		@Override public void widgetSelected (final SelectionEvent e) {
			if (e.widget == updateSize) {
				updateSize();
			}
			else if (e.widget == updateDate) {
				updateDate();
			}
			else if (e.widget == checkValid) {
				checkValid();
			}
			else if (e.widget == newRow) {
				newRow();
			}
			else if (e.widget == deleteRow) {
				deleteRow();
			}
			else if (e.widget == save) {
				saveCSVFile();
			}
		}

		private void updateSize(){
			try{
				System.out.println("==================");
				System.out.println("Updating Sizes");
				System.out.println("==================");
				String readline = null;			
				final BufferedReader in = new BufferedReader(new InputStreamReader(tabledata.openStream()));
				readline = in.readLine();
				int count=0;
				while (readline!=null) {
					HttpURLConnection conn = null;
					System.out.println(Strings.MODDED_URL + readline.split(Strings.COMMA)[0].replace(Strings.SPACE, Strings.UNDERSCORE) + Strings.ZIP_FILES_EXT.substring(1));
					final URL zipurl = new URL(Strings.MODDED_URL + readline.split(Strings.COMMA)[0].replace(Strings.SPACE, Strings.UNDERSCORE) + Strings.ZIP_FILES_EXT.substring(1));
					conn = (HttpURLConnection) zipurl.openConnection();
					conn.setRequestMethod("HEAD");
					conn.getInputStream();
					final long size =conn.getContentLength();					
					table.getItem(count++).setText(3, String.valueOf(size));
					conn.disconnect();
					readline = in.readLine();
				}
				System.out.println("=======DONE=======");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		private void updateDate(){
			try{
				System.out.println("==================");
				System.out.println("Updating Date Modifed");
				System.out.println("==================");
				String readline = null;			
				final BufferedReader in = new BufferedReader(new InputStreamReader(tabledata.openStream()));
				readline = in.readLine();
				int count=0;
				while (readline!=null) {
					HttpURLConnection conn = null;
					System.out.println(Strings.MODDED_URL + readline.split(Strings.COMMA)[0].replace(Strings.SPACE, Strings.UNDERSCORE) + Strings.ZIP_FILES_EXT.substring(1));
					final URL zipurl = new URL(Strings.MODDED_URL + readline.split(Strings.COMMA)[0].replace(Strings.SPACE, Strings.UNDERSCORE) + Strings.ZIP_FILES_EXT.substring(1));
					conn = (HttpURLConnection) zipurl.openConnection();
					conn.setRequestMethod("HEAD");
					conn.getInputStream();
					String date = new SimpleDateFormat(Strings.DATE_FORMAT).format(new Date(conn.getLastModified()));
					table.getItem(count++).setText(4, date);
					conn.disconnect();
					readline = in.readLine();
				}
				System.out.println("=======DONE=======");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		private void checkValid(){
			try{
				System.out.println("==================");
				System.out.println("Seeing if mods are valid");
				System.out.println("==================");	
				int count=0;
				while (count!=table.getItemCount()) {
					System.out.println(Strings.MODDED_URL + table.getItem(count).getText().replace(Strings.SPACE, Strings.UNDERSCORE) + Strings.ZIP_FILES_EXT.substring(1));
					final URL zipurl = new URL(Strings.MODDED_URL + table.getItem(count).getText().replace(Strings.SPACE, Strings.UNDERSCORE) + Strings.ZIP_FILES_EXT.substring(1));
					try{
						zipurl.openStream();
					}catch(Exception e){
						table.getItem(count).setBackground(display.getSystemColor(SWT.COLOR_RED));
						e.printStackTrace();
					}
					count++;
				}
				System.out.println("=======DONE=======");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		private void newRow(){		
			final TableItem item = new TableItem(table, SWT.NONE);
			item.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
			moddatamap.put(item, null);
		}
		private void deleteRow(){
			int count = 0;
			boolean[] checked;
			String[] moddata;

			count = 0;
			checked = new boolean[table.getItems().length];
			moddata = new String[table.getItems().length];

			for (final TableItem item : table.getItems()) {
				checked[count++] = item.getChecked();
			}

			count = 0;
			for (final TableItem item : table.getItems()) {
				moddata[count++] = moddatamap.get(item);
			}

			for (count = table.getItems().length-1;count>=0; count--) {
				if (checked[count]) {
					 moddatamap.remove(moddatamap.get(count));	
					 table.remove(count);
				}
			}
		}
		private void saveCSVFile(){
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] { Strings.CSV_FILES_EXT });
			final String selectedfile = dialog.open();
			
			if (selectedfile != null) {
				System.out.println("==================");
				System.out.println("Saving Table");
				System.out.println("==================");
				try{
					new File(selectedfile).getParentFile().mkdirs();
					File export = new File(selectedfile);
					System.out.println(export.getAbsolutePath());
					FileWriter fw = new FileWriter(export);
					PrintWriter pw = new PrintWriter(fw);
					for (final TableItem item : table.getItems()) {

						for(int i=0; i<table.getColumnCount();i++){
							pw.print(item.getText(i));
							pw.print(",");
						}
						pw.print("\n");
					}
					pw.flush();
					pw.close();
					fw.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.println("=======DONE=======");
			}
		}
		@Override public void widgetDefaultSelected (final SelectionEvent e) {}

	}

	private static final class ExitListener implements Listener {

		@Override public void handleEvent (final Event event) {

			if (!shell.isDisposed()) {

				if (shell.getMaximized()) {

					prefsnode.putBoolean(Strings.PREF_MAX, true);

				} else {

					prefsnode.putInt(Strings.PREF_X, shell.getLocation().x);
					prefsnode.putInt(Strings.PREF_Y, shell.getLocation().y);

					prefsnode.putInt(Strings.PREF_WIDTH, shell.getSize().x);
					prefsnode.putInt(Strings.PREF_HEIGHT, shell.getSize().y);

					prefsnode.putBoolean(Strings.PREF_MAX, false);
				}
			}
			event.doit = true;
			shell.dispose();
			display.dispose();
			System.exit(0);

		}

	}

	private static final class UpdateListener implements SelectionListener, Runnable {

		@Override public void run () {
			display.asyncExec(new Runnable() {

				@Override public void run () {
					setAll(false);
					updateProgress(0, 10);
					System.out.println("==================");
					System.out.println("Exporting Table");
					System.out.println("==================");
					exportTable();
					updateProgress(10, 60);
					System.out.println("==================");
					System.out.println("Uploading");
					System.out.println("==================");
					uploadTable();
					updateProgress(60, 90);
					System.out.println("==================");
					System.out.println("Removing Temp Files");
					System.out.println("==================");
					deleteData();
					updateProgress(90, 100);
					System.out.println("==================");
					System.out.println("Done!");
					System.out.println("==================");
					setAll(true);
					updateProgress(0, 0);
				}
			});
		}

		@Override public void widgetSelected (final SelectionEvent e) {

			new Thread(this).start();

		}

		@Override public void widgetDefaultSelected (final SelectionEvent e) {}

		private static void setAll (final boolean b) {

			display.asyncExec(new Runnable() {

				@Override public void run () {

					checkValid.setEnabled(b);
					updateSize.setEnabled(b);
					updateDate.setEnabled(b);
					newRow.setEnabled(b);
					update.setEnabled(b);
					table.setEnabled(b);

				}

			});

		}

		//upload to ftp
		private static void uploadTable() {
			new FTPupload(host, username,password, Strings.TEMPORARY_DATA_LOCATION_A+"\\"+Strings.MOD_CSV,tablePath);
		}

		//delete remains of program
		private static void deleteData() {
			delete(new File(Strings.TEMPORARY_DATA_LOCATION_A));
		}

		//save table to temp dir
		public static void exportTable(){
			try{
				new File(Strings.TEMPORARY_DATA_LOCATION_A).mkdirs();
				new File(Strings.TEMPORARY_DATA_LOCATION_A).deleteOnExit();
				File export = new File(Strings.TEMPORARY_DATA_LOCATION_A+"\\"+Strings.MOD_CSV);
				System.out.println(export.getAbsolutePath());
				FileWriter fw = new FileWriter(export);
				PrintWriter pw = new PrintWriter(fw);
				for (final TableItem item : table.getItems()) {

					for(int i=0; i<table.getColumnCount();i++){
						pw.print(item.getText(i));
						pw.print(",");
					}
					pw.print("\n");
				}
				pw.flush();
				pw.close();
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		private static void updateProgress (final int from, final int to) {
			display.asyncExec(new Runnable() {
				@Override public void run () {
					for (int i = from ; i < to ; i++) {
						progress.setSelection(i);
						try { TimeUnit.MILLISECONDS.sleep(5); } catch (final InterruptedException e1) {}

					}
					progress.setSelection(to);
				}
			});
		}
		private static void delete (final File f) {

			f.delete();

			if (f.isFile()) return;

			final File[] files = f.getAbsoluteFile().listFiles();

			if (files == null) return;

			for (final File file : files) {

				delete(file);

				f.delete();

			}

		}
	}
}
