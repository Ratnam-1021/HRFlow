package com.hrflow.service;

import com.hrflow.dsa.queue.QueueInterviewScheduler;
import com.hrflow.model.Candidate;
import com.hrflow.model.Interviewer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportExportServiceTest {

    @Test
    public void testExportCandidatesToPdf() throws Exception {
        File tempFile = File.createTempFile("test_candidates_", ".pdf");
        tempFile.deleteOnExit();

        List<Candidate> list = new ArrayList<>();
        Candidate c1 = new Candidate("C1", "Sarah Jenkins", "sarah@example.com", "Lead Software Engineer",
                Set.of("Java", "Spring"), 8.5, 95.0, 92.0, 90.0);
        c1.setStatus(Candidate.CandidateStatus.SHORTLISTED);
        Candidate c2 = new Candidate("C2", "David Chen", "david@example.com", "Senior Full Stack Dev",
                Set.of("Java", "React"), 6.0, 90.0, 90.0, 88.0);
        c2.setStatus(Candidate.CandidateStatus.INTERVIEW_SCHEDULED);
        list.add(c1);
        list.add(c2);

        ReportExportService.exportCandidatesToPdf(
                tempFile,
                "Candidates To Be Shortlisted for HR Interview",
                "Shortlisted candidates awaiting technical evaluation & hiring decisions",
                list
        );

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 500);
    }

    @Test
    public void testExportScheduledToPdf() throws Exception {
        File tempFile = File.createTempFile("test_scheduled_", ".pdf");
        tempFile.deleteOnExit();

        List<QueueInterviewScheduler.InterviewBooking> bookings = new ArrayList<>();
        Candidate c = new Candidate("C1", "Sarah Jenkins", "sarah@example.com", "Lead Software Engineer",
                Set.of("Java", "Spring"), 8.5, 95.0, 92.0, 90.0);
        c.setStatus(Candidate.CandidateStatus.SHORTLISTED);
        Interviewer interviewer = new Interviewer("I1", "Alex Morgan", "Tech Lead", "alex@example.com");
        bookings.add(new QueueInterviewScheduler.InterviewBooking("B1", c, interviewer, java.time.LocalDate.now(), LocalTime.of(9, 0), "Interview Room A"));

        ReportExportService.exportScheduledToPdf(
                tempFile,
                "Candidates Ready For HR Interview",
                "Shortlisted candidates awaiting HR evaluation",
                bookings
        );

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 500);
    }

    public static void main(String[] args) throws Exception {
        String artifactDir = "/Users/ratnamsmac/.gemini/antigravity-ide/brain/93369a19-c0b6-495f-a431-68ba2c9c903a/";
        File candidatePdf = new File(artifactDir, "sample_candidates_report.pdf");
        File scheduledPdf = new File(artifactDir, "sample_scheduled_report.pdf");

        List<Candidate> list = new ArrayList<>();
        Candidate c1 = new Candidate("C1", "Sarah Jenkins", "sarah@example.com", "Lead Software Engineer", Set.of("Java"), 8.5, 95.0, 92.0, 90.0);
        c1.setStatus(Candidate.CandidateStatus.SHORTLISTED);
        Candidate c2 = new Candidate("C2", "David Chen", "david@example.com", "Senior Full Stack Dev", Set.of("React"), 6.0, 90.0, 90.0, 88.0);
        c2.setStatus(Candidate.CandidateStatus.INTERVIEW_SCHEDULED);
        Candidate c3 = new Candidate("C3", "Elena Rostova", "elena@example.com", "DevOps & Cloud Lead", Set.of("AWS"), 7.2, 92.0, 90.0, 89.0);
        c3.setStatus(Candidate.CandidateStatus.SHORTLISTED);
        list.add(c1);
        list.add(c2);
        list.add(c3);

        ReportExportService.exportCandidatesToPdf(
                candidatePdf,
                "Candidates To Be Shortlisted for HR Interview",
                "Shortlisted candidates awaiting technical evaluation & hiring decisions",
                list
        );

        List<QueueInterviewScheduler.InterviewBooking> bookings = new ArrayList<>();
        Interviewer inv1 = new Interviewer("I1", "Alex Morgan", "Tech Lead", "alex@example.com");
        Interviewer inv2 = new Interviewer("I2", "Priya Sharma", "VP of Engineering", "priya@example.com");
        bookings.add(new QueueInterviewScheduler.InterviewBooking("B1", c1, inv1, java.time.LocalDate.now(), LocalTime.of(9, 0), "Interview Room A"));
        bookings.add(new QueueInterviewScheduler.InterviewBooking("B2", c2, inv2, java.time.LocalDate.now(), LocalTime.of(10, 0), "Boardroom B"));

        ReportExportService.exportScheduledToPdf(
                scheduledPdf,
                "Candidates Ready For HR Interview",
                "Shortlisted candidates awaiting HR evaluation",
                bookings
        );

        System.out.println("Generated sample PDFs successfully!");
    }
}
