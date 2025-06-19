package exercisegenerator.view;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.store.*;

public class AlgorithmChooserPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public AlgorithmChooserPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(new JLabel("Auswahl des Algorithmus:"));
        final List<Algorithm> enabledAlgorithms = Algorithm.getEnabledAlgorithms();
        final JList<Algorithm> chooser =
            new JList<Algorithm>(enabledAlgorithms.toArray(new Algorithm[enabledAlgorithms.size()]));
        chooser.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        chooser.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                Store.INSTANCE.setAlgorithms(chooser.getSelectedValuesList());
            }

        });
        this.add(new JScrollPane(chooser));
    }

}
