package net.xiaoyu233.fml.config.editor;

import net.xiaoyu233.fml.config.*;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.function.Consumer;

import static net.xiaoyu233.fml.config.Config.GSON;

public class ConfigEditor extends JFrame {
    private static final Font TREE_FONT = new Font("Default", Font.PLAIN,16);
    private final List<Runnable> saveRuns = new ArrayList<>();
    private final int width = 600,height = 550;
    public ConfigEditor(List<ConfigRegistry> registries) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("所有配置");
        for (ConfigRegistry registry : registries) {
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode("配置文件 " + registry.getPathToConfigFile().getName());
            root.add(defaultMutableTreeNode);
            this.addAllConfigs(registry.getRoot(),defaultMutableTreeNode);
        }
        TreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);
        tree.setFont(TREE_FONT);
        tree.setExpandsSelectedPaths(true);
        tree.setRowHeight(TREE_FONT.getSize() + 5);
        tree.setCellRenderer(new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!leaf){
                    return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                }else {
                    EditingUnit editingUnit = ((ConfigTreeNode) value).editingUnit;
                    editingUnit.setBackground(hasFocus ? this.getBackgroundSelectionColor() : this.getBackgroundNonSelectionColor());
                    return editingUnit;
                }
            }
        });
        tree.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
                if (lastSelectedPathComponent instanceof ConfigTreeNode){
                    Runnable saveChange = ((ConfigTreeNode) lastSelectedPathComponent).editingUnit.saveChange;
                    if (!saveRuns.contains(saveChange)){
                        saveRuns.add(saveChange);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(tree);
        this.setLayout(new BorderLayout());
        this.add(scrollPane,BorderLayout.NORTH);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Box optBox = Box.createHorizontalBox();
        JButton cancel = new JButton("取消");
        cancel.addActionListener(e -> ConfigEditor.this.dispose());
        optBox.add(cancel);
        optBox.add(Box.createVerticalGlue());
        JButton reset = new JButton("重置当前选项");
        reset.addActionListener(e -> {
            Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent instanceof ConfigTreeNode){
                ConfigTreeNode node = (ConfigTreeNode) lastSelectedPathComponent;
                node.editingUnit.reloadValue.accept(node.getConfigEntry().getDefaultValue());
                node.editingUnit.repaint();
                tree.repaint();
            }
        });
        optBox.add(reset);
        optBox.add(Box.createHorizontalGlue());
        JButton save = new JButton("保存并退出");
        save.addActionListener(e -> {
            for (Runnable saveRun : this.saveRuns) {
                saveRun.run();
            }
            for (ConfigRegistry registry : registries) {
                try (FileWriter writer = new FileWriter(registry.getPathToConfigFile())){
                    GSON.toJson(registry.getRoot().write(),writer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                registry.reloadConfig();
            }
            JOptionPane.showMessageDialog(this,"保存成功,更改已生效!");
            ConfigEditor.this.dispose();
        });
        optBox.add(save);
        this.add(optBox,BorderLayout.SOUTH);
        tree.setEditable(true);
        tree.setCellEditor(new DefaultCellEditor(new JTextField()){

            @Override
            public void cancelCellEditing() {
                super.cancelCellEditing();
                Runnable saveChange = ((ConfigTreeNode) tree.getLastSelectedPathComponent()).editingUnit.saveChange;
                if (!saveRuns.contains(saveChange)){
                    saveRuns.add(saveChange);
                }
            }

            @Override
            public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
                if (!leaf){
                    if (!expanded){
                        tree.expandPath(tree.getSelectionPath());
                    }else {
                        tree.collapsePath(tree.getSelectionPath());
                    }
                }
                return tree.getCellRenderer().getTreeCellRendererComponent(tree,value,isSelected,expanded,leaf,row,true);
            }

            //Make can expand/collapse by double click
            @Override
            public boolean isCellEditable(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    TreePath path = tree.getPathForLocation(
                            ((MouseEvent)anEvent).getX(),
                            ((MouseEvent)anEvent).getY());
                    if (path!=null) {
                        Object value = path.getLastPathComponent();
                        TreeModel treeModel = tree.getModel();
                        boolean leaf = treeModel.isLeaf(value);
                        if (!leaf){
                            if ( ((MouseEvent) anEvent).getClickCount() >= 1) {
                                if (tree.isExpanded(path)) {
                                    tree.collapsePath(path);
                                } else {
                                    tree.expandPath(path);
                                }
                            }
                            return false;
                        }else {
                            return true;
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean stopCellEditing() {
                Runnable saveChange = ((ConfigTreeNode) tree.getLastSelectedPathComponent()).editingUnit.saveChange;
                if (!saveRuns.contains(saveChange)){
                    saveRuns.add(saveChange);
                }
                return super.stopCellEditing();
            }
        });
        DisplayMode displayMode = this.getGraphicsConfiguration().getDevice().getDisplayMode();
        this.setBounds((displayMode.getWidth() - this.width) / 2,(displayMode.getHeight() - this.height) / 2,this.width,this.height);
    }

    private void addAllConfigs(Config config, DefaultMutableTreeNode root){
        if (config instanceof ConfigCategory){
            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(config.getName());
            root.add(defaultMutableTreeNode);
            for (Config child : ((ConfigCategory) config).getChild()) {
                addAllConfigs(child,defaultMutableTreeNode);
            }
        } else if (config instanceof ConfigEntry){
            root.add(new ConfigTreeNode((ConfigEntry<?>) config));
        }
    }

    static class ConfigTreeNode extends DefaultMutableTreeNode{
        private final ConfigEntry<?> configEntry;
        private final EditingUnit editingUnit;

        ConfigTreeNode(ConfigEntry<?> configEntry) {
            this.configEntry = configEntry;
            editingUnit = new EditingUnit(this);
        }

        public ConfigEntry<?> getConfigEntry() {
            return configEntry;
        }

        @Override
        public String toString() {
            return configEntry.getComment();
        }
    }



    static class EditingUnit extends Box{
        private Consumer<Object> reloadValue = (t) -> {};
        private Runnable saveChange = () -> {};

        private EditingUnit(ConfigTreeNode value) {
            super(BoxLayout.X_AXIS);
            ConfigEntry<?> configEntry = value.getConfigEntry();
            JLabel comp1 = new JLabel(configEntry.getComment());
            comp1.setFont(TREE_FONT);
            this.add(comp1);
            this.add(Box.createHorizontalGlue());
            JComponent comp = getSuitableEditComponent(configEntry);
            comp.setFont(TREE_FONT);
            this.add(comp);
        }

        @SuppressWarnings("unchecked")
        private JComponent getSuitableEditComponent(ConfigEntry<?> configEntry){
            Codec<?> codec = configEntry.getCodec();
            if (codec == Codec.BOOLEAN) {
                ConfigEntry<Boolean> newEntry = (ConfigEntry<Boolean>) configEntry;
                JCheckBox jCheckBox = new JCheckBox();
                jCheckBox.setBorderPaintedFlat(true);
                jCheckBox.setSelected((Boolean) configEntry.getCurrentValue());
                jCheckBox.setPreferredSize(new Dimension(20,jCheckBox.getHeight()));
                jCheckBox.setBackground(new Color(253, 253, 254, 255));
                saveChange = () -> {
                    newEntry.setCurrentValue(jCheckBox.isSelected());
                    System.out.println("Save change for " + configEntry.getComment());
                };
                reloadValue = (value) -> jCheckBox.setSelected((Boolean) value);
                return jCheckBox;
            }
            if (codec == Codec.DOUBLE || codec == Codec.FLOAT){
                JFormattedTextField jFormattedTextField = new JFormattedTextField(configEntry.getCurrentValue());
                DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory();
                NumberFormat numberInstance = DecimalFormat.getNumberInstance();
                numberInstance.setGroupingUsed(false);
                NumberFormatter atf = new NumberFormatter(numberInstance);
                atf.setAllowsInvalid(false);
                atf.setCommitsOnValidEdit(true);
                defaultFormatterFactory.setDefaultFormatter(atf);
                jFormattedTextField.setFormatterFactory(defaultFormatterFactory);
                Dimension preferredSize = jFormattedTextField.getPreferredSize();
                preferredSize.setSize(preferredSize.getWidth() + 40,preferredSize.getHeight());
                jFormattedTextField.setPreferredSize(preferredSize);
                if (codec == Codec.DOUBLE){
                    saveChange = () -> {
                        ((ConfigEntry<Double>) configEntry).setCurrentValue(((Number) jFormattedTextField.getValue()).doubleValue());
                        System.out.println("Save change for " + configEntry.getComment());
                    };
                }else {
                    saveChange = () -> {
                        ((ConfigEntry<Float>) configEntry).setCurrentValue((((Number) jFormattedTextField.getValue())).floatValue());
                        System.out.println("Save change for " + configEntry.getComment());
                    };
                }
                reloadValue = jFormattedTextField::setValue;
                return jFormattedTextField;
            }
            if (codec == Codec.INTEGER){
                JFormattedTextField jFormattedTextField = new JFormattedTextField(configEntry.getCurrentValue());
                DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory();
                NumberFormat integerInstance = DecimalFormat.getIntegerInstance();
                integerInstance.setGroupingUsed(false);
                NumberFormatter atf = new NumberFormatter(integerInstance);
                atf.setAllowsInvalid(false);
                atf.setCommitsOnValidEdit(true);
                defaultFormatterFactory.setDefaultFormatter(atf);
                jFormattedTextField.setFormatterFactory(defaultFormatterFactory);
                Dimension preferredSize = jFormattedTextField.getPreferredSize();
                preferredSize.setSize(preferredSize.getWidth() + 40,preferredSize.getHeight());
                jFormattedTextField.setPreferredSize(preferredSize);
                saveChange = () -> {
                    ((ConfigEntry<Integer>) configEntry).setCurrentValue(((Number) jFormattedTextField.getValue()).intValue());
                    System.out.println("Save change for " + configEntry.getComment());
                };
                reloadValue = jFormattedTextField::setValue;
                return jFormattedTextField;
            }
            if (codec == Codec.STRING || codec == Codec.FILE) {
                JTextField jTextField = new JTextField(configEntry.getCurrentValue().toString());
                Dimension preferredSize = jTextField.getPreferredSize();
                preferredSize.setSize(preferredSize.getWidth() + 40,preferredSize.getHeight());
                jTextField.setPreferredSize(preferredSize);
                if (codec == Codec.STRING){
                    saveChange = () -> {
                        ((ConfigEntry<String>) configEntry).setCurrentValue(jTextField.getText());
                        System.out.println("Save change for " + configEntry.getComment());
                    };
                }else {
                    saveChange = () -> {
                        ((ConfigEntry<File>) configEntry).setCurrentValue(new File(jTextField.getText()));
                        System.out.println("Save change for " + configEntry.getComment());
                    };
                }
                reloadValue = (value) -> jTextField.setText(value.toString());
                return jTextField;
            }
            throw new IllegalArgumentException("Unsupported codec: " + codec);
        }

        @Override
        public void paint(Graphics g) {
            if (this.getBackground() != null ) {
                g.setColor(this.getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paint(g);
        }
    }
}
