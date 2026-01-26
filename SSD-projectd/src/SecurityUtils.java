public class SecurityUtils {

    // No validation: always returns true
    public static boolean isValidUsername(String username) {
        return !username.isEmpty();
    }

    // No validation: always returns true
    public static boolean isValidPassword(String password) {
        return !password.isEmpty();
    }

    // ---------- Salt + Hash (same as before) ----------
    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        new java.security.SecureRandom().nextBytes(saltBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : saltBytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static String hashPassword(String password, String salt) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            md.update(hexToBytes(salt));
            byte[] hashed = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2)
            out[i / 2] = (byte)((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));

        return out;
    }
}
