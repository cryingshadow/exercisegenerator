package exercisegenerator.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import exercisegenerator.io.*;
import exercisegenerator.store.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private boolean algorithmSelected;

    private boolean directorySelected;

    public MainFrame(final String version) {
        super(String.format("Exercise Generator (Version %s)", version));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        final Container content = this.getContentPane();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.ipady = 10;
        constraints.gridwidth = 4;
        content.add(new JLabel(), constraints);
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        content.add(new JLabel(), constraints);
        constraints.gridx = 1;
        content.add(
            new FileChooserPanel(
                new Runnable() {

                    @Override
                    public void run() {
                        MainFrame.this.pack();
                    }

                }
            ),
            constraints
        );
        constraints.gridx = 2;
        constraints.gridheight = 1;
        content.add(new AlgorithmChooserPanel(), constraints);
        constraints.gridy = 2;
        final SettingsPanel settings = new SettingsPanel();
        content.add(settings, constraints);
        constraints.gridheight = 2;
        constraints.gridx = 3;
        constraints.gridy = 1;
        content.add(new JLabel(), constraints);
        constraints.gridheight = 1;
        constraints.gridwidth = 4;
        constraints.gridx = 0;
        constraints.gridy = 3;
        final JButton generateButton = new JButton("Aufgaben erzeugen");
        Store.INSTANCE.registerAlgorithmObserver(
            algorithms -> {
                this.algorithmSelected = !algorithms.isEmpty();
                generateButton.setEnabled(this.algorithmSelected && this.directorySelected);
            }
        );
        Store.INSTANCE.registerDirectoryObserver(
            directory -> {
                this.directorySelected = directory.isPresent();
                generateButton.setEnabled(this.algorithmSelected && this.directorySelected);
            }
        );
        generateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    final String capacity = settings.getCapacity();
                    if (capacity == null || capacity.isBlank()) {
                        Store.INSTANCE.removeParameter(Flag.CAPACITY);
                    } else {
                        Store.INSTANCE.setParameter(Flag.CAPACITY, capacity);
                    }
                    final String operations = settings.getOperations();
                    if (operations == null || operations.isBlank()) {
                        Store.INSTANCE.removeParameter(Flag.OPERATIONS);
                    } else {
                        Store.INSTANCE.setParameter(Flag.OPERATIONS, operations);
                    }
                    Store.INSTANCE.generatePDFs();
                } catch (final Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, e);
                }
            }

        });
        content.add(generateButton, constraints);
        constraints.gridy = 4;
        content.add(new JLabel(), constraints);
        this.pack();
        this.setLocationRelativeTo(null);
    }

}
