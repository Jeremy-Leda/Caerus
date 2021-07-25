package view.components;

import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 *
 * Composant permettant de déplacer des onglets et de créer des onglets pouvant être fermé
 *
 */
public class DragAndDropCloseableTabbedPane extends JTabbedPane {
    private static final int LINEWIDTH = 3;
    private static final String NAME = "test";
    private final GhostGlassPane glassPane = new GhostGlassPane();
    private final Rectangle lineRect = new Rectangle();
    private final Color lineColor = new Color(0, 100, 255);
    private int dragTabIndex = -1;

    private void clickArrowButton(String actionKey) {
        ActionMap map = getActionMap();
        if (map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(
                        this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }

    private static Rectangle rBackward = new Rectangle();
    private static Rectangle rForward = new Rectangle();
    private static int rwh = 20;
    private static int buttonsize = 30;

    private void autoScrollTest(Point glassPt) {
        Rectangle r = getTabAreaBounds();
        int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP || tabPlacement == BOTTOM) {
            rBackward.setBounds(r.x, r.y, rwh, r.height);
            rForward.setBounds(
                    r.x + r.width - rwh - buttonsize, r.y, rwh + buttonsize, r.height);
        } else if (tabPlacement == LEFT || tabPlacement == RIGHT) {
            rBackward.setBounds(r.x, r.y, r.width, rwh);
            rForward.setBounds(
                    r.x, r.y + r.height - rwh - buttonsize, r.width, rwh + buttonsize);
        }
        rBackward = SwingUtilities.convertRectangle(
                getParent(), rBackward, glassPane);
        rForward = SwingUtilities.convertRectangle(
                getParent(), rForward, glassPane);
        if (rBackward.contains(glassPt)) {
            clickArrowButton("scrollTabsBackwardAction");
        } else if (rForward.contains(glassPt)) {
            clickArrowButton("scrollTabsForwardAction");
        }
    }

    public DragAndDropCloseableTabbedPane(int left, int scrollTabLayout) {
        super(left, scrollTabLayout);
        final DragSourceListener dsl = new DragSourceListener() {
            @Override
            public void dragEnter(DragSourceDragEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            }

            @Override
            public void dragExit(DragSourceEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                lineRect.setRect(0, 0, 0, 0);
                glassPane.setPoint(new Point(-1000, -1000));
                glassPane.repaint();
            }

            @Override
            public void dragOver(DragSourceDragEvent e) {
                Point glassPt = e.getLocation();
                SwingUtilities.convertPointFromScreen(glassPt, glassPane);
                int targetIdx = getTargetTabIndex(glassPt);
                if (getTabAreaBounds().contains(glassPt) && targetIdx >= 0 &&
                        targetIdx != dragTabIndex && targetIdx != dragTabIndex + 1) {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
                    glassPane.setCursor(DragSource.DefaultMoveDrop);
                } else {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                    glassPane.setCursor(DragSource.DefaultMoveNoDrop);
                }
            }

            @Override
            public void dragDropEnd(DragSourceDropEvent e) {
                lineRect.setRect(0, 0, 0, 0);
                dragTabIndex = -1;
                glassPane.setVisible(false);
                if (hasGhost()) {
                    glassPane.setVisible(false);
                    glassPane.setImage(null);
                }
            }

            @Override
            public void dropActionChanged(DragSourceDragEvent e) {
            }
        };
        final Transferable t = new Transferable() {
            private final DataFlavor FLAVOR = new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType, NAME);

            @Override
            public Object getTransferData(DataFlavor flavor) {
                return DragAndDropCloseableTabbedPane.this;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] f = new DataFlavor[1];
                f[0] = this.FLAVOR;
                return f;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.getHumanPresentableName().equals(NAME);
            }
        };
        final DragGestureListener dgl = e -> {
            if (getTabCount() <= 1) return;
            Point tabPt = e.getDragOrigin();
            dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
            if (dragTabIndex < 0 || !isEnabledAt(dragTabIndex)) return;
            initGlassPane(e.getComponent(), e.getDragOrigin());
            try {
                e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
            } catch (InvalidDnDOperationException idoe) {
                idoe.printStackTrace();
            }
        };
        new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE,
                new CDropTargetListener(), true);
        new DragSource().createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
    }

    /**
     * Adds a <code>component</code> with the specified tab title.
     * Cover method for <code>insertTab</code>.
     * This tab is closeable
     *
     * @param title the title to be displayed in this tab
     * @param component the component to be displayed when this tab is clicked
     * @return the component
     *
     * @see #insertTab
     * @see #removeTabAt
     */
    public void addCloseableTab(String title, Component component) {
        this.add(title, component);
        int index = getTabCount() - 1;
        JPanel panel = getPanelTablForClose(title, component);
        setTabComponentAt(index, panel);
    }

    private JPanel getPanelTablForClose(String title,Component component) {
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0,1,0,0);
        pnlTab.add(lblTitle, gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(getCloseButton(component), gbc);
        return pnlTab;
    }

    private JButton getCloseButton(Component component) {
        JButton btnClose = new JButton("x");
        btnClose.setFocusable(true);
        btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setToolTipText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ACTION_BUTTON_LABEL));
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnClose.setBorderPainted(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnClose.setBorderPainted(false);
            }
        });
        btnClose.addActionListener(x -> remove(component));
        return btnClose;
    }

    class CDropTargetListener implements DropTargetListener {
        @Override
        public void dragEnter(DropTargetDragEvent e) {
            if (isDragAcceptable(e)) e.acceptDrag(e.getDropAction());
            else e.rejectDrag();
        }

        @Override
        public void dragExit(DropTargetEvent e) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e) {
        }

        private Point _glassPt = new Point();

        @Override
        public void dragOver(final DropTargetDragEvent e) {
            Point glassPt = e.getLocation();
            if (getTabPlacement() == JTabbedPane.TOP ||
                    getTabPlacement() == JTabbedPane.BOTTOM) {
                initTargetLeftRightLine(getTargetTabIndex(glassPt));
            } else {
                initTargetTopBottomLine(getTargetTabIndex(glassPt));
            }
            if (hasGhost()) {
                glassPane.setPoint(glassPt);
            }
            if (!_glassPt.equals(glassPt)) glassPane.repaint();
            _glassPt = glassPt;
            autoScrollTest(glassPt);
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            if (isDropAcceptable(e)) {
                convertTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
                e.dropComplete(true);
            } else {
                e.dropComplete(false);
            }
            repaint();
        }

        private boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable t = e.getTransferable();
            if (t == null) return false;
            DataFlavor[] f = e.getCurrentDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }

        private boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable t = e.getTransferable();
            if (t == null) return false;
            DataFlavor[] f = t.getTransferDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }
    }

    private boolean hasGhost = true;

    public boolean hasGhost() {
        return hasGhost;
    }

    private boolean isPaintScrollArea = true;

    public boolean isPaintScrollArea() {
        return isPaintScrollArea;
    }

    private int getTargetTabIndex(Point glassPt) {
        Point tabPt = SwingUtilities.convertPoint(
                glassPane, glassPt, DragAndDropCloseableTabbedPane.this);
        boolean isTB = getTabPlacement() == JTabbedPane.TOP ||
                getTabPlacement() == JTabbedPane.BOTTOM;
        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getBoundsAt(i);
            if (isTB) r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            else r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            if (r.contains(tabPt)) return i;
        }
        Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTB) r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
        else r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
        return r.contains(tabPt) ? getTabCount() : -1;
    }

    private void convertTab(int prev, int next) {
        if (next < 0 || prev == next) {
            return;
        }
        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String str = getTitleAt(prev);
        Icon icon = getIconAt(prev);
        String tip = getToolTipTextAt(prev);
        boolean flg = isEnabledAt(prev);
        int tgtindex = prev > next ? next : next - 1;
        remove(prev);
        insertTab(str, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, flg);
        if (flg) setSelectedIndex(tgtindex);
        setTabComponentAt(tgtindex, tab);
    }

    private void initTargetLeftRightLine(int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == 0) {
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(0), glassPane);
            lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
        } else {
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(next - 1), glassPane);
            lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
        }
    }

    private void initTargetTopBottomLine(int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == 0) {
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(0), glassPane);
            lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
        } else {
            Rectangle r = SwingUtilities.convertRectangle(
                    this, getBoundsAt(next - 1), glassPane);
            lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width, LINEWIDTH);
        }
    }

    private void initGlassPane(Component c, Point tabPt) {
        getRootPane().setGlassPane(glassPane);
        if (hasGhost()) {
            Rectangle rect = getBoundsAt(dragTabIndex);
            BufferedImage image = new BufferedImage(
                    c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            c.paint(g);
            rect.x = rect.x < 0 ? 0 : rect.x;
            rect.y = rect.y < 0 ? 0 : rect.y;
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            glassPane.setImage(image);
        }
        Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
        glassPane.setPoint(glassPt);
        glassPane.setVisible(true);
    }

    private Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        Component comp = getSelectedComponent();
        int idx = 0;
        while (comp == null && idx < getTabCount()) comp = getComponentAt(idx++);
        Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
        int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        } else if (tabPlacement == RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.grow(2, 2);
        return tabbedRect;
    }

    class GhostGlassPane extends JPanel {
        private final AlphaComposite composite;
        private Point location = new Point(0, 0);
        private BufferedImage draggingGhost = null;

        public GhostGlassPane() {
            setOpaque(false);
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        }

        public void setImage(BufferedImage draggingGhost) {
            this.draggingGhost = draggingGhost;
        }

        public void setPoint(Point location) {
            this.location = location;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(composite);
            if (isPaintScrollArea() && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
                g2.setPaint(Color.CYAN);
                g2.fill(rBackward);
                g2.fill(rForward);
            }
            if (draggingGhost != null) {
                double xx = location.getX() - (draggingGhost.getWidth(this) / 2d);
                double yy = location.getY() - (draggingGhost.getHeight(this) / 2d);
                g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
            }
            if (dragTabIndex >= 0) {
                g2.setPaint(lineColor);
                g2.fill(lineRect);
            }
        }
    }
}