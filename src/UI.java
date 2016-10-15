import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import entity.Entity3D;
import entity.predefined.BraitenBergVehicle;
import entity.predefined.LightSource;
import entity.predefined.LightSourceManager;
import org.lwjgl.opengl.Display;
import rendering.PickHandler;
import util.SerializableBraitenbergSimulation;
import util.Storage;


public class UI implements Observer {

    private BraitenBergSimulation simulation;

    public static final int VIEWPORT_WIDTH = 1366;
    public static final int VIEWPORT_HEIGHT = 768;

    private BraitenBergVehicle currentVehicle;
    private LightSource currentLightSource;


    public UI()
    {
        currentLightSource = null;
        currentVehicle = null;
        canvas.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        try {
            Display.setParent(canvas);
        }
        catch (Exception e) {
            throw new IllegalStateException();
        }

        setupComponents();

        simulation = new BraitenBergSimulation();
        simulation.pickHandler.addObserver(this);
        updateGUI();
        simulation.run();
    }

    private void setupComponents() {

        panel = new javax.swing.JPanel();
        pauseButton = new javax.swing.JButton();
        simulationSpeedLabel = new javax.swing.JLabel();
        lightAttenuationLabel = new javax.swing.JLabel();
        simulationSpeedSpinner = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        lightSourceLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        vehicleLabel = new javax.swing.JLabel();
        turningForceLabel = new javax.swing.JLabel();
        movementLabel = new javax.swing.JLabel();
        movementComboBox = new javax.swing.JComboBox();
        lightIntensityLabel = new javax.swing.JLabel();
        lightSourceIdLabel = new javax.swing.JLabel();
        vehicleIdLabel = new javax.swing.JLabel();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        simulationSpeedSpinner = new JSpinner(new SpinnerNumberModel(0, 0.00, 10.00, 0.25));
        lightAttenuationSpinner =  new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.2));
        turningForceSpinner = new JSpinner(new SpinnerNumberModel(0.1, 0.1, 1, 0.05));
        lightIntensitySpinner =  new JSpinner(new SpinnerNumberModel(0.1, 0.1, 10, 0.1));

        vehicleIdLabel.setVisible(false);
        vehicleLabel.setVisible(false);
        turningForceSpinner.setVisible(false);
        movementComboBox.setVisible(false);
        turningForceLabel.setVisible(false);
        movementLabel.setVisible(false);

        lightSourceLabel.setVisible(false);
        lightIntensitySpinner.setVisible(false);
        lightIntensityLabel.setVisible(false);
        lightSourceIdLabel.setVisible(false);


        pauseButton.setLabel("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        simulationSpeedLabel.setText("Simulation speed");

        lightAttenuationLabel.setText("Light attenuation");

        lightAttenuationSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                attenuationSpinnerStateChanged(evt);
            }
        });

        simulationSpeedSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speedSpinnerStateChanged(evt);
            }
        });

        lightSourceLabel.setText("Light source");

        vehicleLabel.setText("Vehicle");

        turningForceLabel.setText("Turning force");

        turningForceSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                turningForceSpinnerStateChanged(evt);
            }
        });

        movementLabel.setText("Movement");

        movementComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Towards light", "Away from light"}));
        movementComboBox.addItemListener(new java.awt.event.ItemListener() {
             public void itemStateChanged(java.awt.event.ItemEvent evt) {
                 movementComboBoxItemStateChanged(evt);
             }
        });

        lightIntensityLabel.setText("Light Intensity");

        lightIntensitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lightIntensitySpinnerStateChanged(evt);
            }
        });

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jSeparator1)
                                        .addComponent(jSeparator2)
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(turningForceLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(turningForceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(lightIntensityLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lightIntensitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(lightAttenuationLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lightAttenuationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(simulationSpeedLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(simulationSpeedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(movementLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(movementComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(exportButton, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(vehicleLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(vehicleIdLabel))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(lightSourceLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lightSourceIdLabel)))
                                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(importButton)
                                        .addComponent(exportButton))
                                .addGap(11, 11, 11)
                                .addComponent(pauseButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(simulationSpeedLabel)
                                        .addComponent(simulationSpeedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lightAttenuationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lightAttenuationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lightSourceLabel)
                                        .addComponent(lightSourceIdLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lightIntensityLabel)
                                        .addComponent(lightIntensitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(vehicleLabel)
                                        .addComponent(vehicleIdLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(turningForceLabel)
                                        .addComponent(turningForceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(movementLabel)
                                        .addComponent(movementComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(28, Short.MAX_VALUE))
        );


        frame.setTitle("Braitenberg Simulation");
        frame.add(panel, BorderLayout.EAST);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (simulation.PAUSED) {
            pauseButton.setText("Pause");
        }
        else {
            pauseButton.setText("Resume");
        }

        simulation.PAUSED = !simulation.PAUSED;
    }

    private void speedSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        simulation.TIMESCALE = ((Double) simulationSpeedSpinner.getValue()).floatValue();
    }

    private void attenuationSpinnerStateChanged(ChangeEvent evt) {
        BraitenBergVehicle.LIGHT_ATTENUATION = ((Double) lightAttenuationSpinner.getValue());
    }

    private void turningForceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        if (currentVehicle != null) {
            currentVehicle.TURNING_FORCE = ((Double)turningForceSpinner.getValue()).floatValue();
        }
    }

    private void movementComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (currentVehicle != null) {
            if (movementComboBox.getSelectedItem() == "Towards light") {
                currentVehicle.MOVE_TOWARDS_LIGHT = true;
            }
            else if (movementComboBox.getSelectedItem() == "Away from light") {
                currentVehicle.MOVE_TOWARDS_LIGHT = false;
            }
        }
    }

    private void lightIntensitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        if (currentLightSource != null) {
            currentLightSource.setLightIntensity(((Double)lightIntensitySpinner.getValue()).floatValue());
        }
    }

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {
        SerializableBraitenbergSimulation o = Storage.importFromFile(frame);

        if (o != null) {
            simulation.reloadEntities(o.entities);

            simulation.cam.swapping = true;
            Entity3D.renderSet = o.entities;
            LightSourceManager.clearLightSources();
            int maxId = Integer.MIN_VALUE;
            for (Entity3D e : Entity3D.renderSet) {
                if (e instanceof LightSource) {
                    LightSourceManager.addLightSource((LightSource)e);
                }
                if (e.id > maxId) {
                    maxId = e.id;
                }
            }
            Entity3D.idGen = maxId + 1;
            simulation.TIMESCALE = o.simulationSpeed;
            BraitenBergVehicle.LIGHT_ATTENUATION = o.lightAttenuation;
            simulation.cam.swapping = false;

            currentLightSource = null;
            currentVehicle = null;
            updateGUI();

            if (!simulation.PAUSED) {
                pauseButtonActionPerformed(null);
            }
        }
    }

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {
        SerializableBraitenbergSimulation o
                = new SerializableBraitenbergSimulation(Entity3D.renderSet, simulation.TIMESCALE, BraitenBergVehicle.LIGHT_ATTENUATION);
        Storage.exportToFile(o, frame);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof PickHandler) {
            if (arg instanceof BraitenBergVehicle) {
                BraitenBergVehicle vehicle = (BraitenBergVehicle) arg;
                currentVehicle = vehicle;
                updateGUI();
            }
            else if (arg instanceof LightSource) {
                LightSource lightSource = (LightSource) arg;
                currentLightSource = lightSource;
                updateGUI();
            }
        }
    }

    private void updateGUI() {
        lightAttenuationSpinner.setValue(BraitenBergVehicle.LIGHT_ATTENUATION);
        simulationSpeedSpinner.setValue(simulation.TIMESCALE);

        if (currentVehicle != null) {
            vehicleLabel.setVisible(true);
            vehicleIdLabel.setVisible(true);
            turningForceLabel.setVisible(true);
            turningForceSpinner.setVisible(true);
            movementLabel.setVisible(true);
            movementComboBox.setVisible(true);

            vehicleIdLabel.setText(((Integer) currentVehicle.id).toString());
            turningForceSpinner.setValue((double)currentVehicle.TURNING_FORCE);
            if (currentVehicle.MOVE_TOWARDS_LIGHT) {
                movementComboBox.setSelectedItem("Towards light");
            }
            else {
                movementComboBox.setSelectedItem("Away from light");
            }
        }
        else {
            vehicleLabel.setVisible(false);
            vehicleIdLabel.setVisible(false);
            turningForceLabel.setVisible(false);
            turningForceSpinner.setVisible(false);
            movementLabel.setVisible(false);
            movementComboBox.setVisible(false);
        }


        if (currentLightSource != null) {
            lightSourceIdLabel.setVisible(true);
            lightSourceLabel.setVisible(true);
            lightIntensityLabel.setVisible(true);
            lightIntensitySpinner.setVisible(true);

            lightSourceIdLabel.setText(((Integer) currentLightSource.id).toString());
            lightIntensitySpinner.setValue((double)currentLightSource.getLightIntensity());
        }
        else {
            lightSourceIdLabel.setVisible(false);
            lightSourceLabel.setVisible(false);
            lightIntensityLabel.setVisible(false);
            lightIntensitySpinner.setVisible(false);
        }
    }

    private JFrame frame = new JFrame();
    private Canvas canvas = new Canvas();
    private javax.swing.JSpinner lightAttenuationSpinner;
    private javax.swing.JLabel lightSourceLabel;
    private javax.swing.JLabel lightAttenuationLabel;
    private javax.swing.JLabel vehicleLabel;
    private javax.swing.JLabel turningForceLabel;
    private javax.swing.JLabel movementLabel;
    private javax.swing.JLabel simulationSpeedLabel;
    private javax.swing.JLabel lightIntensityLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSpinner lightIntensitySpinner;
    private javax.swing.JLabel lightSourceIdLabel;
    private javax.swing.JComboBox movementComboBox;
    private javax.swing.JPanel panel;
    private javax.swing.JButton pauseButton;
    private javax.swing.JSpinner simulationSpeedSpinner;
    private javax.swing.JSpinner turningForceSpinner;
    private javax.swing.JLabel vehicleIdLabel;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton importButton;

    public static void main(String args[])
    {
        new UI();
    }
}