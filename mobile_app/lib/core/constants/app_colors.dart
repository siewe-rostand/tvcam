import 'package:flutter/material.dart';

class AppColors {
  // Couleurs primaires - Vert comme dans l'image de référence
  static const Color primary = Color(0xFF10B981);
  static const Color primaryLight = Color(0xFF34D399);
  static const Color primaryDark = Color(0xFF059669);

  // Couleurs secondaires
  static const Color secondary = Color(0xFF3B82F6);
  static const Color secondaryLight = Color(0xFF60A5FA);
  static const Color secondaryDark = Color(0xFF2563EB);

  // Couleurs d'accent
  static const Color accent = Color(0xFFF59E0B);
  static const Color accentLight = Color(0xFFFBBF24);
  static const Color accentDark = Color(0xFFD97706);

  // Couleurs neutres - Thème sombre comme référence
  static const Color background = Color(0xFF0F172A); // Bleu très sombre
  static const Color surface = Color(0xFF1E293B); // Bleu sombre
  static const Color card = Color(0xFFFFFFFF); // Blanc pour les cartes

  // Couleurs de texte
  static const Color textPrimary = Color(0xFFFFFFFF); // Blanc
  static const Color textSecondary = Color(0xFF94A3B8); // Gris clair
  static const Color textTertiary = Color(0xFF64748B); // Gris moyen

  // Couleurs d'état
  static const Color success = Color(0xFF10B981);
  static const Color warning = Color(0xFFF59E0B);
  static const Color error = Color(0xFFEF4444);
  static const Color info = Color(0xFF3B82F6);

  // Couleurs de bordure
  static const Color border = Color(0xFF374151);
  static const Color borderLight = Color(0xFF4B5563);

  // Couleurs d'ombre
  static const Color shadow = Color(0x1A000000);
  static const Color shadowLight = Color(0x0A000000);

  // Couleurs de gradient
  static const LinearGradient primaryGradient = LinearGradient(
    colors: [primary, primaryLight],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  static const LinearGradient secondaryGradient = LinearGradient(
    colors: [secondary, secondaryLight],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  static const LinearGradient accentGradient = LinearGradient(
    colors: [accent, accentLight],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
}
