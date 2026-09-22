package io.github.flamehub.tiktok;

public class TikTokContestUtil {
    
    private static final double POINTS_PER_VIEW = 2.5;
    
    /**
     * Calculates contest points based on view count
     * @param viewCount number of views
     * @return calculated contest points (2.5 points per view)
     */
    public static double calculateContestPoints(long viewCount) {
        return viewCount * POINTS_PER_VIEW;
    }
    
    /**
     * Formats contest points for display
     * @param points contest points to format
     * @return formatted string with points
     */
    public static String formatContestPoints(double points) {
        return String.format("%.1f", points);
    }
    
}
