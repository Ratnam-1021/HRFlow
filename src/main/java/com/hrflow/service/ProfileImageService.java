package com.hrflow.service;

import com.hrflow.model.Candidate;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.InputStream;
import java.util.*;

/**
 * Service to manage real, professional candidate profile avatars
 * with automatic gender detection (male/female) and deterministic avatar assignment.
 */
public class ProfileImageService {

    private static final List<String> MALE_IMAGES = List.of(
            "/com/hrflow/images/profiles/male/v3_0003963.jpg",
            "/com/hrflow/images/profiles/male/v3_0077195.jpg",
            "/com/hrflow/images/profiles/male/v3_0209734.jpg",
            "/com/hrflow/images/profiles/male/v3_0232171.jpg",
            "/com/hrflow/images/profiles/male/v3_0261429.jpg",
            "/com/hrflow/images/profiles/male/v3_0451538.jpg",
            "/com/hrflow/images/profiles/male/v3_0538485.jpg",
            "/com/hrflow/images/profiles/male/v3_0634124.jpg",
            "/com/hrflow/images/profiles/male/v3_0657605.jpg",
            "/com/hrflow/images/profiles/male/v3_0753498.jpg",
            "/com/hrflow/images/profiles/male/v3_0765191.jpg",
            "/com/hrflow/images/profiles/male/v3_0774458.jpg",
            "/com/hrflow/images/profiles/male/v3_0912933.jpg",
            "/com/hrflow/images/profiles/male/v3_0977431.jpg"
    );

    private static final List<String> FEMALE_IMAGES = List.of(
            "/com/hrflow/images/profiles/female/v3_0056235.jpg",
            "/com/hrflow/images/profiles/female/v3_0240962.jpg",
            "/com/hrflow/images/profiles/female/v3_0305260.jpg",
            "/com/hrflow/images/profiles/female/v3_0354625.jpg",
            "/com/hrflow/images/profiles/female/v3_0361913.jpg",
            "/com/hrflow/images/profiles/female/v3_0371289.jpg",
            "/com/hrflow/images/profiles/female/v3_0405375.jpg",
            "/com/hrflow/images/profiles/female/v3_0425287.jpg",
            "/com/hrflow/images/profiles/female/v3_0466549.jpg",
            "/com/hrflow/images/profiles/female/v3_0489587.jpg",
            "/com/hrflow/images/profiles/female/v3_0494341.jpg",
            "/com/hrflow/images/profiles/female/v3_0754957.jpg",
            "/com/hrflow/images/profiles/female/v3_0874645.jpg",
            "/com/hrflow/images/profiles/female/v3_0935103.jpg"
    );

    private static final Set<String> FEMALE_NAMES = Set.of(
            "aditi", "deepika", "isha", "meera", "pooja", "sanya", "zoya",
            "priya", "ananya", "neha", "sneha", "kavya", "riya", "shreya", "tanvi",
            "elena", "sarah", "emma", "olivia", "sophia", "charlotte", "amelia"
    );

    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    public static boolean isFemale(String fullName) {
        if (fullName == null || fullName.isBlank()) return false;
        String firstName = fullName.trim().split("\\s+")[0].toLowerCase();
        return FEMALE_NAMES.contains(firstName);
    }

    public static Image getProfileImage(Candidate candidate) {
        if (candidate == null) return null;
        return getProfileImage(candidate.getId(), candidate.getName());
    }

    public static Image getProfileImage(String id, String name) {
        boolean female = isFemale(name);
        List<String> pool = female ? FEMALE_IMAGES : MALE_IMAGES;
        int seed = (id != null ? id : (name != null ? name : "0")).hashCode();
        int idx = Math.abs(seed) % pool.size();
        String path = pool.get(idx);

        return IMAGE_CACHE.computeIfAbsent(path, p -> {
            try (InputStream is = ProfileImageService.class.getResourceAsStream(p)) {
                if (is != null) {
                    return new Image(is, 128, 128, true, true);
                }
            } catch (Exception ignored) {}
            return null;
        });
    }

    public static Node createAvatarNode(Candidate candidate, double size) {
        return createAvatarNode(candidate != null ? candidate.getId() : null,
                candidate != null ? candidate.getName() : "Candidate", size);
    }

    public static Node createAvatarNode(String id, String name, double size) {
        Image img = getProfileImage(id, name);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            Circle clip = new Circle(size / 2.0, size / 2.0, size / 2.0);
            iv.setClip(clip);

            Circle border = new Circle(size / 2.0, size / 2.0, size / 2.0);
            border.setFill(null);
            border.setStroke(javafx.scene.paint.Color.web("#E2D4C4"));
            border.setStrokeWidth(1.2);

            StackPane stack = new StackPane(iv, border);
            stack.setPrefSize(size, size);
            stack.setMaxSize(size, size);
            return stack;
        }

        // Fallback to initials badge
        Label av = new Label(getInitials(name));
        av.setPrefSize(size, size);
        av.setMinSize(size, size);
        av.setAlignment(Pos.CENTER);
        av.setStyle("-fx-background-color: #FFF2E8; -fx-text-fill: #EA580C; -fx-font-weight: 800; -fx-font-size: " + (size * 0.38) + "px; -fx-background-radius: 50%; -fx-border-color: #F3E2D2; -fx-border-radius: 50%; -fx-border-width: 1.2px;");
        return av;
    }

    private static String getInitials(String name) {
        if (name == null || name.isBlank()) return "C";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
