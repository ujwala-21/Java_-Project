package com.bookloop.ui.student;

import com.bookloop.controller.BookController;
import com.bookloop.model.BookListing;
import com.bookloop.util.ImageUtil;
import com.bookloop.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;

public class EditListingDialog extends JDialog {
    private final BookController bookController = new BookController();
    private final BookListing listing;
    private boolean updated = false;

    private JComboBox<String> typeCombo;
    private JComboBox<String> conditionCombo;
    private JTextField priceField;
    private JTextField rentalDaysField;
    private JLabel rentalDaysLabel;
    private JLabel priceLabel;
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

    public EditListingDialog(JFrame parent, BookListing listing) {
        super(parent, "Edit Listing — " + (listing.getBook() != null ? listing.getBook().getTitle() : "Book"), true);
        this.listing = listing;
        setSize(680, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
        initComponents();
        populateFromListing();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtil.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Header
        JLabel titleLbl = UIUtil.createTitleLabel("Edit Book Listing ✏️");
        String bookTitle = listing.getBook() != null ? listing.getBook().getTitle() : "Unknown Book";
        JLabel bookNameLbl = UIUtil.createSubtitleLabel(bookTitle);
        bookNameLbl.setForeground(UIUtil.ACCENT_CORAL);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLbl);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(bookNameLbl);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        root.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new BorderLayout(16, 0));
        formPanel.setOpaque(false);

        // Left: Image
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(200, 400));

        JLabel imgSection = UIUtil.createHeadingLabel("Cover Photo");
        imgSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imgSection);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        imagePreviewLabel = new JLabel(ImageUtil.loadAndScaleImage(listing.getImagePath(), listing.getBookId(), 180, 230));
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imagePreviewLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton uploadBtn = UIUtil.createSecondaryButton("Change Cover 📸");
        uploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBtn.addActionListener(e -> chooseImage());
        leftPanel.add(uploadBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        fileNameLabel = UIUtil.createMutedLabel(listing.getImagePath() != null ? "Current cover" : "No cover");
        fileNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(fileNameLabel);

        JButton removeBtn = UIUtil.createGhostButton("Remove Cover ❌");
        removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeBtn.addActionListener(e -> {
            selectedImageFile = null;
            listing.setImagePath(null);
            imagePreviewLabel.setIcon(ImageUtil.createPlaceholder(180, 230, "📷"));
            fileNameLabel.setText("No cover");
        });
        leftPanel.add(removeBtn);

        formPanel.add(leftPanel, BorderLayout.WEST);

        // Right: Fields
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Listing Type
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        rightPanel.add(UIUtil.createBodyLabel("Listing Type *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        typeCombo = UIUtil.createComboBox(new String[]{"SELL", "EXCHANGE", "RENT", "DONATE"});
        typeCombo.addActionListener(e -> updatePricingFields());
        rightPanel.add(typeCombo, gbc);

        // Price / Credits
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        priceLabel = UIUtil.createBodyLabel("Selling Price (₹) *");
        rightPanel.add(priceLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        priceField = UIUtil.createTextField("e.g. 250.00");
        rightPanel.add(priceField, gbc);

        // Rental Duration
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        rentalDaysLabel = UIUtil.createBodyLabel("Rental Duration (days)");
        rentalDaysLabel.setVisible(false);
        rightPanel.add(rentalDaysLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        rentalDaysField = UIUtil.createTextField("14");
        rentalDaysField.setVisible(false);
        rightPanel.add(rentalDaysField, gbc);

        // Condition
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        rightPanel.add(UIUtil.createBodyLabel("Condition *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        conditionCombo = UIUtil.createComboBox(new String[]{"NEW", "EXCELLENT", "GOOD", "ACCEPTABLE"});
        rightPanel.add(conditionCombo, gbc);

        // Condition Flags
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
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
        gbc.gridwidth = 1;

        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        rightPanel.add(UIUtil.createBodyLabel("Description"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        descArea = UIUtil.createTextArea("Notes about the book...", 3);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(280, 70));
        rightPanel.add(descScroll, gbc);

        formPanel.add(rightPanel, BorderLayout.CENTER);
        root.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        buttonPanel.setOpaque(false);

        JButton cancelBtn = UIUtil.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        JButton saveBtn = UIUtil.createPrimaryButton("Save Changes 💾");
        saveBtn.addActionListener(e -> saveChanges());
        buttonPanel.add(saveBtn);

        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void populateFromListing() {
        typeCombo.setSelectedItem(listing.getListingType().name());
        conditionCombo.setSelectedItem(listing.getConditionType().name());

        switch (listing.getListingType()) {
            case SELL -> priceField.setText(listing.getAskingPrice() != null ? listing.getAskingPrice().toPlainString() : "");
            case EXCHANGE -> priceField.setText(listing.getCreditValue() != null ? listing.getCreditValue().toPlainString() : "");
            case RENT -> {
                priceField.setText(listing.getRentalCredits() != null ? listing.getRentalCredits().toPlainString() : "");
                rentalDaysField.setText(listing.getRentalDurationDays() != null ? String.valueOf(listing.getRentalDurationDays()) : "14");
            }
            case DONATE -> priceField.setText("0.00");
        }

        cbHighlights.setSelected(listing.isHasHighlights());
        cbAnnotations.setSelected(listing.isHasAnnotations());
        cbMissingPages.setSelected(listing.isHasMissingPages());
        cbDamagedCover.setSelected(listing.isHasDamagedCover());
        cbTornPages.setSelected(listing.isHasTornPages());
        cbWaterDamage.setSelected(listing.isHasWaterDamage());

        descArea.setText(listing.getDescription() != null ? listing.getDescription() : "");

        updatePricingFields();
    }

    private void updatePricingFields() {
        String type = (String) typeCombo.getSelectedItem();
        if ("SELL".equals(type)) {
            priceLabel.setText("Selling Price (₹) *");
            priceField.setEnabled(true);
            rentalDaysLabel.setVisible(false);
            rentalDaysField.setVisible(false);
        } else if ("EXCHANGE".equals(type)) {
            priceLabel.setText("Exchange Credits");
            priceField.setEnabled(true);
            rentalDaysLabel.setVisible(false);
            rentalDaysField.setVisible(false);
        } else if ("RENT".equals(type)) {
            priceLabel.setText("Rental Fee (₹) *");
            priceField.setEnabled(true);
            rentalDaysLabel.setVisible(true);
            rentalDaysField.setVisible(true);
        } else if ("DONATE".equals(type)) {
            priceLabel.setText("Cost: Free (Donation)");
            priceField.setText("0.00");
            priceField.setEnabled(false);
            rentalDaysLabel.setVisible(false);
            rentalDaysField.setVisible(false);
        }
        revalidate();
        repaint();
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
            ImageIcon icon = ImageUtil.loadAndScaleImage(selectedImageFile.getAbsolutePath(), 180, 230);
            imagePreviewLabel.setIcon(icon);
            long kb = selectedImageFile.length() / 1024;
            String name = selectedImageFile.getName();
            if (name.length() > 20) name = name.substring(0, 17) + "...";
            fileNameLabel.setText(name + " (" + kb + " KB)");
        }
    }

    private void saveChanges() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String type = (String) typeCombo.getSelectedItem();
                String cond = (String) conditionCombo.getSelectedItem();

                listing.setListingType(BookListing.ListingType.valueOf(type));
                listing.setConditionType(BookListing.ConditionType.valueOf(cond));
                listing.setDescription(descArea.getText().trim());

                listing.setHasHighlights(cbHighlights.isSelected());
                listing.setHasAnnotations(cbAnnotations.isSelected());
                listing.setHasMissingPages(cbMissingPages.isSelected());
                listing.setHasDamagedCover(cbDamagedCover.isSelected());
                listing.setHasTornPages(cbTornPages.isSelected());
                listing.setHasWaterDamage(cbWaterDamage.isSelected());

                // Reset pricing
                listing.setAskingPrice(null);
                listing.setCreditValue(null);
                listing.setRentalCredits(null);
                listing.setRentalDurationDays(null);

                if ("SELL".equals(type)) {
                    String val = priceField.getText().trim();
                    if (val.isEmpty()) throw new Exception("Selling price is required.");
                    listing.setAskingPrice(new BigDecimal(val));
                } else if ("EXCHANGE".equals(type)) {
                    String val = priceField.getText().trim();
                    if (!val.isEmpty()) listing.setCreditValue(new BigDecimal(val));
                } else if ("RENT".equals(type)) {
                    String val = priceField.getText().trim();
                    if (val.isEmpty()) throw new Exception("Rental fee is required.");
                    listing.setRentalCredits(new BigDecimal(val));
                    listing.setRentalDurationDays(Integer.parseInt(rentalDaysField.getText().trim()));
                }

                // Image update
                if (selectedImageFile != null) {
                    String savedPath = ImageUtil.saveImage(selectedImageFile);
                    listing.setImagePath(savedPath);
                }

                return bookController.updateListing(listing);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        updated = true;
                        UIUtil.showSuccess(EditListingDialog.this, "Listing updated successfully! ✅");
                        dispose();
                    }
                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    if (ex.getCause() != null) msg = ex.getCause().getMessage();
                    UIUtil.showError(EditListingDialog.this, "Failed to update: " + msg);
                }
            }
        };
        worker.execute();
    }

    public boolean wasUpdated() {
        return updated;
    }
}
