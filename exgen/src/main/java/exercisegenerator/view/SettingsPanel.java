package exercisegenerator.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import exercisegenerator.io.*;
import exercisegenerator.store.*;

public class SettingsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField capacity;

    private final JTextField operations;

    public SettingsPanel() {
        this.setLayout(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 10;
        constraints.ipady = 10;
        constraints.gridwidth = 2;
        this.add(new JLabel("Einstellungen:"), constraints);
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        this.add(new JLabel("Anzahl Aufgaben:"), constraints);
        constraints.gridx = 1;
        final JSpinner numOfExercises = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        numOfExercises.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                Store.INSTANCE.setParameter(Flag.NUMBER, String.valueOf(numOfExercises.getValue()));
            }

        });
        this.add(numOfExercises, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        this.add(new JLabel("Instanzlänge:"), constraints);
        constraints.gridx = 1;
        final JSpinner length = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        length.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                final int value = (Integer)length.getValue();
                if (value == 0) {
                    Store.INSTANCE.removeParameter(Flag.LENGTH);
                } else {
                    Store.INSTANCE.setParameter(Flag.LENGTH, String.valueOf(value));
                }
            }

        });
        this.add(length, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;
        this.add(new JLabel("Kapazität/Bitlänge:"), constraints);
        constraints.gridx = 1;
        this.capacity = new JTextField(12);
        this.add(this.capacity, constraints);
        constraints.gridx = 0;
        constraints.gridy = 4;
        this.add(new JLabel("Grad:"), constraints);
        constraints.gridx = 1;
        final JSpinner degree = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        degree.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                final int value = (Integer)degree.getValue();
                if (value == 0) {
                    Store.INSTANCE.removeParameter(Flag.DEGREE);
                } else {
                    Store.INSTANCE.setParameter(Flag.DEGREE, String.valueOf(value));
                }
            }

        });
        this.add(degree, constraints);
        constraints.gridx = 0;
        constraints.gridy = 5;
        this.add(new JLabel("Operationen/Code:"), constraints);
        constraints.gridx = 1;
        this.operations = new JTextField(12);
        this.add(this.operations, constraints);
    }

    public String getCapacity() {
        return this.capacity.getText();
    }

    public String getOperations() {
        return this.operations.getText();
    }

}
