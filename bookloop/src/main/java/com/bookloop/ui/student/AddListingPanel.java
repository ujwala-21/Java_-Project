package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.model.Book;
import com.bookloop.model.BookListing;
import com.bookloop.model.Department;
import com.bookloop.util.ImageUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddListingPanel extends JPanel {
    private final BookController bookController = new BookController();
    private final Runnable onListingCreated;

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField editionField;
    private JTextField subjectField;
    private JComboBox<String> deptCombo;
    private List<Department> departmentList = new ArrayList<>();

    private JComboBox<String> typeCombo;
    private JComboBox<String> conditionCombo;
    private JTextField originalPriceField;
    private JTextField priceOrCreditField;
    private JLabel priceOrCreditLabel;
    private JTextField rentalDurationField;
    private JLabel rentalDurationLabel;
    private JTextArea descArea;

    private JCheckBox cbHighlights;
    private JCheckBox cbAnnotations;
    private JCheckBox cbMissingPages;
    private JCheckBox cbDamagedCover;
    private JCheckBox cbTornPages;
    private JCheckBox cbWaterDamage;

    private File selectedImageFile;
    private JLabel imagePreviewLabel;
    private JLabel fileNameLabel;
    private JLabel inlinePhotoStatusLbl;
    private JButton removePhotoBtn;
    private JButton submitBtn;

    public AddListingPanel(Runnable onListingCreated) {
        this.onListingCreated = onListingCreated;
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
        JLabel titleLbl = UIUtil.createTitleLabel("List a Book on BookLoop 📖");
        JLabel subLbl = UIUtil.createMutedLabel("Pass your textbooks forward to another student in need");
        header.add(titleLbl, BorderLayout.NORTH);
        header.add(subLbl, BorderLayout.SOUTH);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createRigidArea(new Dimension(0, 16)));

        // Form Card (Split: Left Image Upload / Right Inputs)
        JPanel formCard = UIUtil.createCard();
        formCard.setLayout(new BorderLayout(24, 0));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.setMaximumSize(new Dimension(950, 700));

        // LEFT: IMAGE UPLOAD PREVIEW
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(240, 480));

        JLabel imgSection = UIUtil.createHeadingLabel("Cover Photo");
        imgSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imgSection);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        imagePreviewLabel = new JLabel(ImageUtil.createPlaceholder(220, 280, "📷"));
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imagePreviewLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton uploadBtn = UIUtil.createSecondaryButton("Upload Book Cover 📸");
        uploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBtn.addActionListener(e -> chooseImage());
        leftPanel.add(uploadBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        fileNameLabel = UIUtil.createMutedLabel("No photo selected");
        fileNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(fileNameLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        removePhotoBtn = UIUtil.createSecondaryButton("Remove Photo ❌");
        removePhotoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        removePhotoBtn.setVisible(false);
        removePhotoBtn.addActionListener(e -> removeSelectedPhoto());
        leftPanel.add(removePhotoBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel tipLbl = UIUtil.createMutedLabel("<html><center>Supports JPG, PNG, WEBP<br>Automatic resize & storage</center></html>");
        tipLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(tipLbl);

        formCard.add(leftPanel, BorderLayout.WEST);

        // RIGHT: INPUT FIELDS GRID
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Section 1: Book Info
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel sec1 = UIUtil.createHeadingLabel("1. Book Information");
        sec1.setForeground(UIUtil.ACCENT_CORAL);
        rightPanel.add(sec1, gbc);
        gbc.gridwidth = 1;

        // Title
        gbc.gridx = 0; gbc.gridy = 1;
        rightPanel.add(UIUtil.createBodyLabel("Book Title *"), gbc);
        gbc.gridx = 1;
        titleField = UIUtil.createTextField("e.g. Operating System Concepts");
        rightPanel.add(titleField, gbc);

        // Author
        gbc.gridx = 0; gbc.gridy = 2;
        rightPanel.add(UIUtil.createBodyLabel("Author(s) *"), gbc);
        gbc.gridx = 1;
        authorField = UIUtil.createTextField("e.g. Silberschatz, Galvin");
        rightPanel.add(authorField, gbc);

        // Department & Subject
        gbc.gridx = 0; gbc.gridy = 3;
        rightPanel.add(UIUtil.createBodyLabel("Department"), gbc);
        gbc.gridx = 1;
        deptCombo = UIUtil.createComboBox(new String[]{"Select Department"});
        rightPanel.add(deptCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        rightPanel.add(UIUtil.createBodyLabel("Subject / Course"), gbc);
        gbc.gridx = 1;
        subjectField = UIUtil.createTextField("e.g. Computer Science / OS");
        rightPanel.add(subjectField, gbc);

        // Edition & ISBN
        gbc.gridx = 0; gbc.gridy = 5;
        rightPanel.add(UIUtil.createBodyLabel("Edition & ISBN"), gbc);
        gbc.gridx = 1;
        JPanel edIsbnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        edIsbnPanel.setOpaque(false);
        editionField = UIUtil.createTextField("10th Edition");
        isbnField = UIUtil.createTextField("ISBN (optional)");
        edIsbnPanel.add(editionField);
        edIsbnPanel.add(isbnField);
        rightPanel.add(edIsbnPanel, gbc);

        // Cover Photo row in the form
        gbc.gridx = 0; gbc.gridy = 6;
        rightPanel.add(UIUtil.createBodyLabel("Cover Photo 📸"), gbc);
        gbc.gridx = 1;
        JPanel photoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        photoRow.setOpaque(false);
        JButton inlineUploadBtn = UIUtil.createSecondaryButton("Choose Cover Image 🖼️");
        inlineUploadBtn.addActionListener(e -> chooseImage());
        photoRow.add(inlineUploadBtn);
        inlinePhotoStatusLbl = UIUtil.createMutedLabel("(Optional - JPG/PNG supported)");
        photoRow.add(inlinePhotoStatusLbl);
        rightPanel.add(photoRow, gbc);

        // Section 2: Listing Type & Pricing
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JLabel sec2 = UIUtil.createHeadingLabel("2. Listing Option & Pricing");
        sec2.setForeground(UIUtil.ACCENT_CORAL);
        rightPanel.add(sec2, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 8;
        rightPanel.add(UIUtil.createBodyLabel("Listing Type *"), gbc);
        gbc.gridx = 1;
        typeCombo = UIUtil.createComboBox(new String[]{"SELL", "EXCHANGE", "RENT", "DONATE"});
        typeCombo.addActionListener(e -> updatePricingFields());
        rightPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        priceOrCreditLabel = UIUtil.createBodyLabel("Selling Price (₹) *");
        rightPanel.add(priceOrCreditLabel, gbc);
        gbc.gridx = 1;
        priceOrCreditField = UIUtil.createTextField("e.g. 250.00");
        rightPanel.add(priceOrCreditField, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        rentalDurationLabel = UIUtil.createBodyLabel("Rental Duration (days)");
        rentalDurationLabel.setVisible(false);
        rightPanel.add(rentalDurationLabel, gbc);
        gbc.gridx = 1;
        rentalDurationField = UIUtil.createTextField("14");
        rentalDurationField.setVisible(false);
        rightPanel.add(rentalDurationField, gbc);

        // Section 3: Condition Transparency
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        JLabel sec3 = UIUtil.createHeadingLabel("3. Condition Transparency");
        sec3.setForeground(UIUtil.ACCENT_CORAL);
        rightPanel.add(sec3, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 12;
        rightPanel.add(UIUtil.createBodyLabel("Condition *"), gbc);
        gbc.gridx = 1;
        conditionCombo = UIUtil.createComboBox(new String[]{"EXCELLENT", "NEW", "GOOD", "ACCEPTABLE"});
        rightPanel.add(conditionCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 13; gbc.gridwidth = 2;
        JPanel checkGrid = new JPanel(new GridLayout(2, 3, 4, 4));
        checkGrid.setOpaque(false);
        cbHighlights = new JCheckBox("Highlighting");
        cbAnnotations = new JCheckBox("Annotations");
        cbMissingPages = new JCheckBox("Missing Pages");
        cbDamagedCover = new JCheckBox("Damaged Cover");
        cbTornPages = new JCheckBox("Torn Pages");
        cbWaterDamage = new JCheckBox("Water Damage");
        checkGrid.add(cbHighlights);
        checkGrid.add(cbAnnotations);
        checkGrid.add(cbMissingPages);
        checkGrid.add(cbDamagedCover);
        checkGrid.add(cbTornPages);
        checkGrid.add(cbWaterDamage);
        rightPanel.add(checkGrid, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 14; gbc.gridwidth = 1;
        rightPanel.add(UIUtil.createBodyLabel("Notes / Description"), gbc);
        gbc.gridx = 1;
        descArea = UIUtil.createTextArea("Describe book state, edition notes, or pickup preference...", 3);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(280, 60));
        rightPanel.add(descScroll, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 15; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        submitBtn = UIUtil.createPrimaryButton("Publish Book Listing 🚀");
        submitBtn.setPreferredSize(new Dimension(submitBtn.getPreferredSize().width, 42));
        submitBtn.addActionListener(e -> publishListing());
        rightPanel.add(submitBtn, gbc);

        formCard.add(rightPanel, BorderLayout.CENTER);
        container.add(formCard);

        JScrollPane scrollPane = UIUtil.createScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updatePricingFields() {
        String type = (String) typeCombo.getSelectedItem();
        if ("SELL".equals(type)) {
            priceOrCreditLabel.setText("Selling Price (₹) *");
            priceOrCreditField.setText("");
            priceOrCreditField.setEnabled(true);
            rentalDurationLabel.setVisible(false);
            rentalDurationField.setVisible(false);
        } else if ("EXCHANGE".equals(type)) {
            priceOrCreditLabel.setText("Exchange Credits (leave blank to auto-calculate)");
            priceOrCreditField.setText("");
            priceOrCreditField.setEnabled(true);
            rentalDurationLabel.setVisible(false);
            rentalDurationField.setVisible(false);
        } else if ("RENT".equals(type)) {
            priceOrCreditLabel.setText("Rental Fee (₹) *");
            priceOrCreditField.setText("50.00");
            priceOrCreditField.setEnabled(true);
            rentalDurationLabel.setVisible(true);
            rentalDurationField.setVisible(true);
        } else if ("DONATE".equals(type)) {
            priceOrCreditLabel.setText("Cost: Free (Donation)");
            priceOrCreditField.setText("0.00");
            priceOrCreditField.setEnabled(false);
            rentalDurationLabel.setVisible(false);
            rentalDurationField.setVisible(false);
        }
        revalidate();
        repaint();
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
                    deptCombo.addItem("General / Any Department");
                    for (Department d : departmentList) {
                        deptCombo.addItem(d.getName());
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Book Cover Image");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files (*.jpg, *.png, *.jpeg, *.webp)", "jpg", "jpeg", "png", "webp"));
        chooser.setAcceptAllFileFilterUsed(false);

        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            ImageIcon icon = ImageUtil.loadAndScaleImage(selectedImageFile.getAbsolutePath(), 220, 280);
            imagePreviewLabel.setIcon(icon);
            long kb = selectedImageFile.length() / 1024;
            String name = selectedImageFile.getName();
            if (name.length() > 20) name = name.substring(0, 17) + "...";
            fileNameLabel.setText(name + " (" + kb + " KB)");
            if (inlinePhotoStatusLbl != null) {
                inlinePhotoStatusLbl.setText(name + " (" + kb + " KB) ✅");
                inlinePhotoStatusLbl.setForeground(new Color(0x059669));
            }
            removePhotoBtn.setVisible(true);
        }
    }

    private void removeSelectedPhoto() {
        selectedImageFile = null;
        imagePreviewLabel.setIcon(ImageUtil.createPlaceholder(220, 280, "📷"));
        fileNameLabel.setText("No photo selected");
        if (inlinePhotoStatusLbl != null) {
            inlinePhotoStatusLbl.setText("(Optional - JPG/PNG supported)");
            inlinePhotoStatusLbl.setForeground(UIUtil.TEXT_SECONDARY);
        }
        removePhotoBtn.setVisible(false);
    }

    private void publishListing() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String cond = (String) conditionCombo.getSelectedItem();

        if (title.isEmpty() || author.isEmpty()) {
            UIUtil.showError(this, "Please enter both Book Title and Author.");
            return;
        }

        submitBtn.setEnabled(false);
        submitBtn.setText("Publishing...");

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(author);
                book.setIsbn(isbnField.getText().trim());
                book.setEdition(editionField.getText().trim());
                book.setSubject(subjectField.getText().trim());

                int deptIdx = deptCombo.getSelectedIndex();
                if (deptIdx > 0 && deptIdx - 1 < departmentList.size()) {
                    book.setDepartmentId(departmentList.get(deptIdx - 1).getId());
                }

                BookListing listing = new BookListing();
                listing.setListingType(BookListing.ListingType.valueOf(type));
                listing.setConditionType(BookListing.ConditionType.valueOf(cond));
                listing.setDescription(descArea.getText().trim());

                // Condition flags
                listing.setHasHighlights(cbHighlights.isSelected());
                listing.setHasAnnotations(cbAnnotations.isSelected());
                listing.setHasMissingPages(cbMissingPages.isSelected());
                listing.setHasDamagedCover(cbDamagedCover.isSelected());
                listing.setHasTornPages(cbTornPages.isSelected());
                listing.setHasWaterDamage(cbWaterDamage.isSelected());

                // Pricing
                if ("SELL".equals(type)) {
                    listing.setAskingPrice(new BigDecimal(priceOrCreditField.getText().trim()));
                } else if ("EXCHANGE".equals(type)) {
                    String credStr = priceOrCreditField.getText().trim();
                    if (!credStr.isEmpty()) {
                        listing.setCreditValue(new BigDecimal(credStr));
                    }
                } else if ("RENT".equals(type)) {
                    listing.setRentalCredits(new BigDecimal(priceOrCreditField.getText().trim()));
                    listing.setRentalDurationDays(Integer.parseInt(rentalDurationField.getText().trim()));
                }

                // Image saving
                if (selectedImageFile != null) {
                    String savedPath = ImageUtil.saveImage(selectedImageFile);
                    listing.setImagePath(savedPath);
                    book.setCoverImage(savedPath);
                }

                return bookController.createBookAndListing(book, listing);
            }

            @Override
            protected void done() {
                submitBtn.setEnabled(true);
                submitBtn.setText("Publish Book Listing 🚀");
                try {
                    int id = get();
                    if (id > 0) {
                        UIUtil.showSuccess(AddListingPanel.this, "Your book has been listed successfully!");
                        resetForm();
                        if (onListingCreated != null) onListingCreated.run();
                    }
                } catch (Exception ex) {
                    UIUtil.showError(AddListingPanel.this, ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void resetForm() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        editionField.setText("");
        subjectField.setText("");
        priceOrCreditField.setText("");
        descArea.setText("");
        cbHighlights.setSelected(false);
        cbAnnotations.setSelected(false);
        cbMissingPages.setSelected(false);
        cbDamagedCover.setSelected(false);
        cbTornPages.setSelected(false);
        cbWaterDamage.setSelected(false);
        removeSelectedPhoto();
    }
}
