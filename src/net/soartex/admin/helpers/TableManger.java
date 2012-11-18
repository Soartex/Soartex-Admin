package net.soartex.admin.helpers;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public class TableManger {
	public TableManger(final Table table){

		// create a TableCursor to navigate around the table
		final TableCursor cursor = new TableCursor(table, SWT.NONE);
		// create an editor to edit the cell when the user hits "ENTER" 
		// while over a cell in the table
		final ControlEditor editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;

		cursor.addSelectionListener(new SelectionAdapter() {
			// when the TableEditor is over a cell, select the corresponding row in 
			// the table
			public void widgetSelected(SelectionEvent e) {
				table.setSelection(new TableItem[] { cursor.getRow()});
			}
			// when the user hits "ENTER" in the TableCursor, pop up a text editor so that 
			// they can change the text of the cell
			public void widgetDefaultSelected(SelectionEvent e) {
				final Text text = new Text(cursor, SWT.NONE);
				TableItem row = cursor.getRow();
				int column = cursor.getColumn();
				text.setText(row.getText(column));
				text.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						// close the text editor and copy the data over 
						// when the user hits "ENTER"
						if (e.character == SWT.CR) {
							TableItem row = cursor.getRow();
							int column = cursor.getColumn();
							row.setText(column, text.getText());
							text.dispose();
						}
						// close the text editor when the user hits "ESC"
						if (e.character == SWT.ESC) {
							text.dispose();
						}
					}
				});
				// close the text editor when the user tabs away
				text.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						text.dispose();
					}
				});
				editor.setEditor(text);
				text.setFocus();
			}
		});
		// Hide the TableCursor when the user hits the "CTRL" or "SHIFT" key.
		// This allows the user to select multiple items in the table.
		cursor.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CTRL
						|| e.keyCode == SWT.SHIFT
						|| (e.stateMask & SWT.CONTROL) != 0
						|| (e.stateMask & SWT.SHIFT) != 0) {
					cursor.setVisible(false);
				}
			}
		});
		// When the user double clicks in the TableCursor, pop up a text editor so that 
		// they can change the text of the cell.
		cursor.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				final Text text = new Text(cursor, SWT.NONE);
				TableItem row = cursor.getRow();
				int column = cursor.getColumn();
				text.setText(row.getText(column));
				text.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						// close the text editor and copy the data over 
						// when the user hits "ENTER"
						if (e.character == SWT.CR) {
							TableItem row = cursor.getRow();
							int column = cursor.getColumn();
							row.setText(column, text.getText());
							text.dispose();
						}
						// close the text editor when the user hits "ESC"
						if (e.character == SWT.ESC) {
							text.dispose();
						}
					}
				});
				// close the text editor when the user clicks away
				text.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						text.dispose();
					}
				});
				editor.setEditor(text);
				text.setFocus();
			}
		});

		// Show the TableCursor when the user releases the "SHIFT" or "CTRL" key.
		// This signals the end of the multiple selection task.
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CONTROL && (e.stateMask & SWT.SHIFT) != 0)
					return;
				if (e.keyCode == SWT.SHIFT && (e.stateMask & SWT.CONTROL) != 0)
					return;
				if (e.keyCode != SWT.CONTROL
						&& (e.stateMask & SWT.CONTROL) != 0)
					return;
				if (e.keyCode != SWT.SHIFT && (e.stateMask & SWT.SHIFT) != 0)
					return;

				TableItem[] selection = table.getSelection();
				TableItem row = (selection.length == 0) ? table.getItem(table.getTopIndex()) : selection[0];
				table.showItem(row);
				cursor.setSelection(row, 0);
				cursor.setVisible(true);
				cursor.setFocus();
			}
		});
	}
}
