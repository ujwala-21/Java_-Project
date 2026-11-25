package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.model.Book;
import com.bookloop.model.Department;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationsPanel extends JPanel {
    private final BookController bookController = new BookController();
    private final Runnable onBrowseMarketplace;

    private JComboBox<String> deptCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> semCombo;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private List<Department> departmentList = new ArrayList<>();
    private List<Book> recommendedBooks = new ArrayList<>();

    public RecommendationsPanel(Runnable onBrowseMarketplace) {
        this.onBrowseMarketplace = onBrowseMarketplace;
        setLayout(new BorderLayout());
        setBackground(UIUtil.BG_PRIMARY);
        initComponents();
        loadDepartments();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIUtil.BG_PRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLbl = UIUtil.createTitleLabel("Semester Course Book Recommendations 🎓");
        JLabel subLbl = UIUtil.createMutedLabel("Discover verified textbooks recommended by faculty for your semester");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 16)));

        // Selector Bar Card
        JPanel filterCard = UIUtil.createCard();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filterCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel dLbl = UIUtil.createBodyLabel("Department:");
        deptCombo = UIUtil.createComboBox(new String[]{"Loading..."});
        deptCombo.setPreferredSize(new Dimension(220, 36));

        JLabel yLbl = UIUtil.createBodyLabel("Year:");
        yearCombo = UIUtil.createComboBox(new String[]{"Year 1", "Year 2", "Year 3", "Year 4"});
        yearCombo.setPreferredSize(new Dimension(100, 36));

        JLabel sLbl = UIUtil.createBodyLabel("Semester:");
        semCombo = UIUtil.createComboBox(new String[]{"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"});
        semCombo.setPreferredSize(new Dimension(100, 36));

        JButton findBtn = UIUtil.createPrimaryButton("Find Books 🔍");
        findBtn.addActionListener(e -> fetchRecommendations());

        filterCard.add(dLbl);
        filterCard.add(deptCombo);
        filterCard.add(yLbl);
        filterCard.add(yearCombo);
        filterCard.add(sLbl);
        filterCard.add(semCombo);
        filterCard.add(findBtn);

        container.add(filterCard);
        container.add(Box.createRigidArea(new Dimension(0, 14)));

        // Table Card
        JPanel tableCard = UIUtil.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] cols = {"Book ID", "Title", "Author", "Subject", "Edition", "ISBN", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        booksTable = new JTable(tableModel);
        UIUtil.styleTable(booksTable);
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        booksTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane sp = UIUtil.createScrollPane(booksTable);
        sp.setPreferredSize(new Dimension(850, 380));
        tableCard.add(sp, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);
        JButton wishBtn = UIUtil.createSecondaryButton("Add Selected to Wishlist ❤️");
        wishBtn.addActionListener(e -> addSelectedToWishlist());
        bottomBar.add(wishBtn);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        container.add(tableCard);

        JScrollPane outerScroll = UIUtil.createScrollPane(container);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
    }

    private void loadDepartments() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Department> doInBackground() throws Exception {
                return bookController.getDepartments();
            }

            @Override
            protected void done() {
                try {
                    departmentList = get();
                    deptCombo.removeAllItems();
                    for (Department d : departmentList) {
                        deptCombo.addItem(d.getName());
                    }
                    if (!departmentList.isEmpty()) {
                        fetchRecommendations();
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void fetchRecommendations() {
        int deptIdx = deptCombo.getSelectedIndex();
        if (deptIdx < 0 || deptIdx >= departmentList.size()) return;

        int deptId = departmentList.get(deptIdx).getId();
        int year = yearCombo.getSelectedIndex() + 1;
        int sem = semCombo.getSelectedIndex() + 1;

        SwingWorker<List<Book>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                return bookController.getSemesterRecommendations(deptId, year, sem);
            }

            @Override
            protected void done() {
                try {
                    recommendedBooks = get();
                    tableModel.setRowCount(0);
                    for (Book b : recommendedBooks) {
                        tableModel.addRow(new Object[]{
                            b.getId(),
                            b.getTitle(),
                            b.getAuthor(),
                            b.getSubject() != null ? b.getSubject() : "Core Subject",
                            b.getEdition() != null ? b.getEdition() : "Standard",
                            b.getIsbn() != null ? b.getIsbn() : "N/A",
                            "Available in Catalog"
                        });
                    }
                } catch (Exception ex) {
                    UIUtil.showError(RecommendationsPanel.this, "Failed to load recommendations: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void addSelectedToWishlist() {
        int row = booksTable.getSelectedRow();
        if (row < 0) {
            UIUtil.showError(this, "Please select a book from the table first.");
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return bookController.addToWishlist(bookId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIUtil.showSuccess(RecommendationsPanel.this, "Added \"" + title + "\" to your wishlist!");
                    }
                } catch (Exception ex) {
                    UIUtil.showError(RecommendationsPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
