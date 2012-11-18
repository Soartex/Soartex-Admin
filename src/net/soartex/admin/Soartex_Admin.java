package net.soartex.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.TimeUnit;

import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.soartex.admin.console.TextAreaOutputStream;
import net.soartex.admin.helpers.TableManger;
import net.soartex.admin.listeners.PrimaryListener;

import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class Soartex_Admin {

	// TODO: Program Variables

	private static final Preferences prefsnode = Preferences.userNodeForPackage(Soartex_Admin.class).node(Strings.SOARTEX_ADMIN);

	private static URL tabledata;

	private static HashMap<TableItem, String> moddatamap;

	// TODO: SWT Components

	private static Display display;
	private static Shell shell;
	private static JFrame frame;

	private static Button technic;
	private static Button ftb;
	private static Button all;
	private static Button none;

	public static Table table;

	public static TableColumn name;
	public static TableColumn version;
	public static TableColumn gameversion;
	public static TableColumn size;
	public static TableColumn modified;

	public static Button update;

	private static ProgressBar progress;

	// TODO: Menu Items

	// TODO: Methods

	public static void main (final String[] args) {

		initializeLogger();

		initializeShell();

		initializeComponents();

		startEventLoop();
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

		shell.setLayout(new GridLayout(4, true));

	}

	private static void initializeComponents () {

		// TODO: Selection Buttons
		GridData gd = new GridData();
		
		technic = new Button(shell, SWT.PUSH);
		ftb = new Button(shell, SWT.PUSH);
		all = new Button(shell, SWT.PUSH);
		none = new Button(shell, SWT.PUSH);

		technic.setText(Strings.TECHNIC_BUTTON);
		ftb.setText(Strings.FTB_BUTTON);
		all.setText(Strings.ALL_BUTTON);
		none.setText(Strings.NONE_BUTTON);

		final SelectButtonsListener sblistener = new SelectButtonsListener();

		technic.addSelectionListener(sblistener);
		ftb.addSelectionListener(sblistener);
		all.addSelectionListener(sblistener);
		none.addSelectionListener(sblistener);

		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		technic.setLayoutData(gd);
		ftb.setLayoutData(gd);
		all.setLayoutData(gd);
		none.setLayoutData(gd);


		// TODO: Mod Table

		table = new Table(shell, SWT.BORDER | SWT.CHECK|SWT.FULL_SELECTION);
		name = new TableColumn(table, SWT.CENTER);
		version = new TableColumn(table, SWT.CENTER);
		gameversion = new TableColumn(table, SWT.CENTER);
		size = new TableColumn(table, SWT.CENTER);
		modified = new TableColumn(table, SWT.CENTER);

		new TableManger(table);
		
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

		// TODO: Patch Button

		update = new Button(shell, SWT.PUSH);

		update.setText(Strings.PATCH_BUTTON);

		update.addSelectionListener(new UpdateListener());

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;

		update.setLayoutData(gd);

		update.setEnabled(false);

		// TODO: Progress Bar

		progress = new ProgressBar(shell, SWT.NORMAL);

		progress.setLayoutData(gd);

	}

	private static void loadTable () {
		try{
			String readline = null;
			//iteminfo storage
			ArrayList<String[]> itemsInfo = new ArrayList<String[]>();
			tabledata = new URL(Strings.MODDED_URL + Strings.MOD_CSV);
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
					itemtext[1] = "Unknown";
				}

				//gameversion
				try{
					itemtext[2] = readline.split(Strings.COMMA)[2];
				} catch(final Exception e){
					itemtext[1] = "Unknown";
				}

				//size
				try{
					String temp= readline.split(Strings.COMMA)[3];
					final long size = Integer.parseInt(temp);

					if (size > 1024 && size < 1048576 ) itemtext[3] = String.valueOf(size / 1024) + Strings.KILOBYTES;
					else if (size > 1048576) itemtext[3] = String.valueOf(size / (1048576)) + Strings.MEGABYTES;
					else itemtext[3] = String.valueOf(size) + Strings.BYTES;
				} catch(final Exception e){
					itemtext[1] = "Unknown";
				}					

				//date modified
				try{
					itemtext[4] = readline.split(Strings.COMMA)[4];
				} catch(final Exception e){
					itemtext[4] = "Unknown";
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
				try {
					Thread.sleep(10);
					shell.update();
				} catch (InterruptedException e) {}
			}
			name.pack();
			gameversion.pack();
			version.pack();
			size.pack();
			modified.pack();
			System.out.println("=======DONE=======");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadTableInfo (final ArrayList<String[]> a, final ArrayList<String> b) {

		display.syncExec(new Runnable() {
			@Override public void run () {
				for(int i=0; i<a.size();i++){
					final TableItem item = new TableItem(table, SWT.NONE);
					item.setText(a.get(i));
					moddatamap.put(item, b.get(i));
				}
				name.pack();
				gameversion.pack();
				version.pack();
				size.pack();
				modified.pack();
			}});
	}

	private static void startEventLoop () {

		shell.open();

		while (!shell.isDisposed()) {

			if (!display.readAndDispatch()) display.sleep();

		}
		frame.dispose();
	}

	// TODO: Listeners

	private static final class SelectButtonsListener implements SelectionListener {

		@Override public void widgetSelected (final SelectionEvent e) {

			if (e.widget == all || e.widget == none) {

				for (final TableItem item : table.getItems()) {

					item.setChecked(e.widget == all);

				}

			} else if (e.widget == technic) {

				try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(Strings.MODDED_URL + Strings.TECHNIC_LIST).openStream()))) {

					for (final TableItem item : table.getItems()) {

						item.setChecked(false);

					}

					String readline = in.readLine();

					while (readline != null) {

						for (final TableItem item : table.getItems()) {

							if (readline.contains(item.getText())) item.setChecked(true);

						}

						readline = in.readLine();

					}

				} catch (final IOException e1) {

					e1.printStackTrace();

				}

			} else if (e.widget == ftb) {

				try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(Strings.MODDED_URL + Strings.FTB_LIST).openStream()))) {

					for (final TableItem item : table.getItems()) {

						item.setChecked(false);

					}

					String readline = in.readLine();

					while (readline != null) {

						for (final TableItem item : table.getItems()) {

							if (readline.contains(item.getText())) item.setChecked(true);

						}

						readline = in.readLine();

					}

				} catch (final IOException e1) {

					e1.printStackTrace();

				}

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
			new Thread(new Runnable() {

				@Override public void run () { 
					setAll(false);
					updateProgress(0, 10);
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					
					
					updateProgress(10, 25);
					updateProgress(25, 35);
					updateProgress(35, 60);
					updateProgress(60, 75);
					updateProgress(75, 100);

					System.out.println("==================");
					System.out.println("Done!");
					System.out.println("==================");
					setAll(true);

					try {

						TimeUnit.SECONDS.sleep(2);

					} catch (final InterruptedException e) {

						e.printStackTrace();

					} finally {

						updateProgress(0, 0);

					}
				}

			}).start();
		}

		@Override public void widgetSelected (final SelectionEvent e) {

			new Thread(this).start();

		}

		@Override public void widgetDefaultSelected (final SelectionEvent e) {}

		private static void setAll (final boolean b) {

			display.asyncExec(new Runnable() {

				@Override public void run () {

					technic.setEnabled(b);
					ftb.setEnabled(b);
					all.setEnabled(b);
					none.setEnabled(b);
					update.setEnabled(b);
					table.setEnabled(b);

				}

			});

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

	}
}
