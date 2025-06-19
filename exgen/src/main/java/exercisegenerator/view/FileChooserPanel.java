package exercisegenerator.view;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import exercisegenerator.store.*;

public class FileChooserPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public FileChooserPanel(final Runnable packer) {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Auswahl des Verzeichnisses:"));
        final JLabel path = new JLabel();
        this.add(path);
        Store.INSTANCE.registerDirectoryObserver(
            directory -> path.setText(directory.map(File::getAbsolutePath).orElse("kein Verzeichnis ausgewählt"))
        );
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Verzeichnis wählen");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        final JButton button = new JButton("Verzeichnis auswählen");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (chooser.showDialog(FileChooserPanel.this, "Verzeichnis auswählen") == JFileChooser.APPROVE_OPTION) {
                    Store.INSTANCE.setDirectory(chooser.getSelectedFile());
                }
                packer.run();
            }

        });
        this.add(button);
    }

}
