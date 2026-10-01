package com.hrflow.service;

import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.model.Candidate;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service to generate high-fidelity, executive-styled direct PDF export reports
 * matching HRFlow's presentation design.
 */
public class ReportExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Corporate Color Palette matching UI design
    private static final Color COLOR_NAVY = new Color(0x10, 0x35, 0x5E);
    private static final Color COLOR_ICE_BLUE = new Color(0xDD, 0xEB, 0xF7);
    private static final Color COLOR_SECTION_BG = new Color(0xF8, 0xFA, 0xFC);
    private static final Color COLOR_BORDER = new Color(0xCB, 0xD5, 0xE1);
    private static final Color COLOR_TEXT_DARK = new Color(0x0F, 0x17, 0x2A);
    private static final Color COLOR_TEXT_MUTED = new Color(0x47, 0x55, 0x69);
    private static final Color COLOR_SCORE_BLUE = new Color(0x1D, 0x4E, 0xD8);
    private static final Color COLOR_STATUS_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color COLOR_STATUS_TEXT = new Color(0x15, 0x80, 0x3D);
    private static final Color COLOR_TIME_ORANGE = new Color(0xC2, 0x41, 0x0C);
    private static final Color COLOR_ROOM_GOLD = new Color(0x92, 0x40, 0x0E);

    /**
     * Directly generates an executive PDF report for Candidates.
     * Contains:
     * 1. Top Navy Banner with custom Heading
     * 2. Ice Blue Purpose Bar with custom Purpose text
     * 3. Metadata row (Generated timestamp & Total Candidates count)
     * 4. Ranked Candidates Section Header & Table with Green Status cells
     */
    public static void exportCandidatesToPdf(File file, String title, String purpose, List<Candidate> candidates) throws Exception {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{ 8f, 23f, 25f, 15f, 14f, 20f });

        // 1. Top Header Banner
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase(title.toUpperCase(), fontTitle));
        headerCell.setColspan(6);
        headerCell.setBackgroundColor(COLOR_NAVY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPaddingTop(9f);
        headerCell.setPaddingBottom(9f);
        headerCell.setBorderColor(COLOR_BORDER);
        table.addCell(headerCell);

        // 2. Purpose Banner
        String purposeDisplay = purpose.startsWith("Purpose:") ? purpose : "Purpose: " + purpose;
        Font fontPurpose = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, new Color(0x1E, 0x29, 0x3B));
        PdfPCell purposeCell = new PdfPCell(new Phrase(purposeDisplay, fontPurpose));
        purposeCell.setColspan(6);
        purposeCell.setBackgroundColor(COLOR_ICE_BLUE);
        purposeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        purposeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        purposeCell.setPaddingTop(7f);
        purposeCell.setPaddingBottom(7f);
        purposeCell.setBorderColor(COLOR_BORDER);
        table.addCell(purposeCell);

        // 3. Metadata Row
        Font fontMeta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_TEXT_MUTED);
        PdfPCell metaLeft = new PdfPCell(new Phrase("Generated: " + timestamp, fontMeta));
        metaLeft.setColspan(3);
        metaLeft.setBackgroundColor(Color.WHITE);
        metaLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
        metaLeft.setPadding(6f);
        metaLeft.setBorderColor(COLOR_BORDER);
        table.addCell(metaLeft);

        PdfPCell metaRight = new PdfPCell(new Phrase("Total Candidates: " + candidates.size(), fontMeta));
        metaRight.setColspan(3);
        metaRight.setBackgroundColor(Color.WHITE);
        metaRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        metaRight.setPadding(6f);
        metaRight.setBorderColor(COLOR_BORDER);
        table.addCell(metaRight);

        // 4. Section Title: RANKED CANDIDATES
        Font fontSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, COLOR_NAVY);
        PdfPCell sectionCell = new PdfPCell(new Phrase("RANKED CANDIDATES", fontSection));
        sectionCell.setColspan(6);
        sectionCell.setBackgroundColor(COLOR_SECTION_BG);
        sectionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionCell.setPadding(6f);
        sectionCell.setBorderColor(COLOR_BORDER);
        table.addCell(sectionCell);

        // 5. Table Header Row
        String[] headers = { "Rank", "Candidate Name", "Applied Role", "Experience (Years)", "Overall Score (%)", "Status" };
        Font fontColHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, Color.WHITE);
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, fontColHeader));
            c.setBackgroundColor(COLOR_NAVY);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setVerticalAlignment(Element.ALIGN_MIDDLE);
            c.setPadding(7f);
            c.setBorderColor(COLOR_BORDER);
            table.addCell(c);
        }

        // 6. Candidate Data Rows
        Font fontRank = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, COLOR_NAVY);
        Font fontName = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, COLOR_TEXT_DARK);
        Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 9f, COLOR_TEXT_DARK);
        Font fontScore = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, COLOR_SCORE_BLUE);
        Font fontStatus = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_STATUS_TEXT);

        int rank = 1;
        for (Candidate c : candidates) {
            String statusText = "INTERVIEW SCHEDULED";
            if (c.getStatus() == Candidate.CandidateStatus.SHORTLISTED) {
                statusText = "SHORTLISTED";
            } else if (c.getStatus() != null) {
                statusText = c.getStatus().name().replace('_', ' ');
            }

            PdfPCell cellRank = createCell(String.valueOf(rank++), fontRank, Element.ALIGN_CENTER, Color.WHITE);
            PdfPCell cellName = createCell(c.getName(), fontName, Element.ALIGN_CENTER, Color.WHITE);
            PdfPCell cellRole = createCell(c.getAppliedRole(), fontRegular, Element.ALIGN_CENTER, Color.WHITE);
            PdfPCell cellExp = createCell(String.format("%.1f", c.getExperienceYears()), fontRegular, Element.ALIGN_CENTER, Color.WHITE);
            PdfPCell cellScore = createCell(String.format("%.1f%%", c.getOverallScore()), fontScore, Element.ALIGN_CENTER, Color.WHITE);
            PdfPCell cellStatus = createCell(statusText, fontStatus, Element.ALIGN_CENTER, COLOR_STATUS_BG);

            table.addCell(cellRank);
            table.addCell(cellName);
            table.addCell(cellRole);
            table.addCell(cellExp);
            table.addCell(cellScore);
            table.addCell(cellStatus);
        }

        document.add(table);
        document.close();
    }

    /**
     * Directly generates an executive PDF report for Scheduled Interviews.
     */
    public static void exportScheduledToPdf(File file, String title, String purpose, List<QueueInterviewScheduler.InterviewBooking> bookings) throws Exception {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{ 7f, 13f, 21f, 18f, 20f, 14f, 17f });

        // 1. Top Header Banner
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase(title.toUpperCase(), fontTitle));
        headerCell.setColspan(7);
        headerCell.setBackgroundColor(COLOR_NAVY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPaddingTop(9f);
        headerCell.setPaddingBottom(9f);
        headerCell.setBorderColor(COLOR_BORDER);
        table.addCell(headerCell);

        // 2. Purpose Banner
        String purposeDisplay = purpose.startsWith("Purpose:") ? purpose : "Purpose: " + purpose;
        Font fontPurpose = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, new Color(0x1E, 0x29, 0x3B));
        PdfPCell purposeCell = new PdfPCell(new Phrase(purposeDisplay, fontPurpose));
        purposeCell.setColspan(7);
        purposeCell.setBackgroundColor(COLOR_ICE_BLUE);
        purposeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        purposeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        purposeCell.setPaddingTop(7f);
        purposeCell.setPaddingBottom(7f);
        purposeCell.setBorderColor(COLOR_BORDER);
        table.addCell(purposeCell);

        // 3. Metadata Row
        Font fontMeta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_TEXT_MUTED);
        PdfPCell metaLeft = new PdfPCell(new Phrase("Generated: " + timestamp, fontMeta));
        metaLeft.setColspan(4);
        metaLeft.setBackgroundColor(Color.WHITE);
        metaLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
        metaLeft.setPadding(6f);
        metaLeft.setBorderColor(COLOR_BORDER);
        table.addCell(metaLeft);

        PdfPCell metaRight = new PdfPCell(new Phrase("Total Scheduled Sessions: " + bookings.size(), fontMeta));
        metaRight.setColspan(3);
        metaRight.setBackgroundColor(Color.WHITE);
        metaRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        metaRight.setPadding(6f);
        metaRight.setBorderColor(COLOR_BORDER);
        table.addCell(metaRight);

        // 4. Section Title: SCHEDULED CANDIDATES
        Font fontSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, COLOR_NAVY);
        PdfPCell sectionCell = new PdfPCell(new Phrase("SCHEDULED CANDIDATES", fontSection));
        sectionCell.setColspan(7);
        sectionCell.setBackgroundColor(COLOR_SECTION_BG);
        sectionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionCell.setPadding(6f);
        sectionCell.setBorderColor(COLOR_BORDER);
        table.addCell(sectionCell);

        // 5. Table Header Row
        String[] headers = { "Slot", "Time Slot", "Candidate Name", "Applied Role", "Assigned Interviewer", "Allocated Room", "Status" };
        Font fontColHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, Color.WHITE);
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, fontColHeader));
            c.setBackgroundColor(COLOR_NAVY);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setVerticalAlignment(Element.ALIGN_MIDDLE);
            c.setPadding(7f);
            c.setBorderColor(COLOR_BORDER);
            table.addCell(c);
        }

        // 6. Data Rows
        Font fontSlot = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, COLOR_NAVY);
        Font fontTime = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, COLOR_TIME_ORANGE);
        Font fontName = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, COLOR_TEXT_DARK);
        Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, COLOR_TEXT_DARK);
        Font fontRoom = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_ROOM_GOLD);
        Font fontStatus = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_STATUS_TEXT);

        int slot = 1;
        for (QueueInterviewScheduler.InterviewBooking b : bookings) {
            String candidateName = b.getCandidate() != null ? b.getCandidate().getName() : "";
            String role = b.getCandidate() != null ? b.getCandidate().getAppliedRole() : "";
            String interviewerInfo = b.getInterviewer() != null ? (b.getInterviewer().getName() + " (" + b.getInterviewer().getRole() + ")") : "";
            String time = b.getTimeSlot() != null ? b.getTimeSlot().toString() : "";
            String room = b.getRoom() != null ? b.getRoom() : "";

            table.addCell(createCell(String.valueOf(slot++), fontSlot, Element.ALIGN_CENTER, Color.WHITE));
            table.addCell(createCell(time, fontTime, Element.ALIGN_CENTER, Color.WHITE));
            table.addCell(createCell(candidateName, fontName, Element.ALIGN_CENTER, Color.WHITE));
            table.addCell(createCell(role, fontRegular, Element.ALIGN_CENTER, Color.WHITE));
            table.addCell(createCell(interviewerInfo, fontRegular, Element.ALIGN_CENTER, Color.WHITE));
            table.addCell(createCell(room, fontRoom, Element.ALIGN_CENTER, Color.WHITE));
            table.addCell(createCell("INTERVIEW SCHEDULED", fontStatus, Element.ALIGN_CENTER, COLOR_STATUS_BG));
        }

        document.add(table);
        document.close();
    }

    private static PdfPCell createCell(String text, Font font, int alignment, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setPaddingTop(7.5f);
        cell.setPaddingBottom(7.5f);
        cell.setPaddingLeft(5f);
        cell.setPaddingRight(5f);
        cell.setBorderColor(COLOR_BORDER);
        return cell;
    }

    public static void exportCandidatesToCsv(File file, String title, String purpose, List<Candidate> candidates) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.println("# " + title);
            writer.println("# Purpose: " + purpose);
            writer.println("# Generated: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.println("# Total Candidates: " + (candidates != null ? candidates.size() : 0));
            writer.println("Rank,Candidate Name,Applied Role,Experience (Years),Overall Score (%),Status");
            if (candidates != null) {
                int rank = 1;
                for (Candidate c : candidates) {
                    writer.printf("%d,%s,%s,%.1f,%.1f%%,%s%n",
                            rank++,
                            escapeCsv(c.getName()),
                            escapeCsv(c.getAppliedRole()),
                            c.getExperienceYears(),
                            c.getOverallScore(),
                            c.getStatus() != null ? c.getStatus().name() : "");
                }
            }
        }
    }

    public static void exportScheduledToCsv(File file, String title, String purpose, List<QueueInterviewScheduler.InterviewBooking> bookings) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.println("# " + title);
            writer.println("# Purpose: " + purpose);
            writer.println("# Generated: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.println("# Total Scheduled Sessions: " + (bookings != null ? bookings.size() : 0));
            writer.println("Slot,Time Slot,Candidate Name,Applied Role,Assigned Interviewer,Allocated Room,Status");
            if (bookings != null) {
                int slot = 1;
                for (QueueInterviewScheduler.InterviewBooking b : bookings) {
                    writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                            slot++,
                            escapeCsv(b.getTimeSlot() != null ? b.getTimeSlot().toString() : ""),
                            escapeCsv(b.getCandidate() != null ? b.getCandidate().getName() : ""),
                            escapeCsv(b.getCandidate() != null ? b.getCandidate().getAppliedRole() : ""),
                            escapeCsv(b.getInterviewer() != null ? b.getInterviewer().getName() + " (" + b.getInterviewer().getRole() + ")" : ""),
                            escapeCsv(b.getRoom() != null ? b.getRoom() : ""),
                            "INTERVIEW SCHEDULED");
                }
            }
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}

