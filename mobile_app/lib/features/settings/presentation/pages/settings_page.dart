import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/constants/app_colors.dart';

class SettingsPage extends ConsumerStatefulWidget {
  const SettingsPage({super.key});

  @override
  ConsumerState<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends ConsumerState<SettingsPage> {
  bool _notificationsEnabled = true;
  bool _darkModeEnabled = false;
  bool _autoSyncEnabled = true;
  bool _offlineModeEnabled = false;
  String _selectedLanguage = 'Français';
  String _selectedQuality = 'Haute qualité';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('Paramètres'),
        backgroundColor: AppColors.surface,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Section Général
            _buildSection(
              'Général',
              Icons.settings,
              [
                _buildLanguageSetting(),
                const SizedBox(height: 16),
                _buildThemeSetting(),
                const SizedBox(height: 16),
                _buildQualitySetting(),
              ],
            ),
            
            const SizedBox(height: 32),
            
            // Section Notifications
            _buildSection(
              'Notifications',
              Icons.notifications,
              [
                _buildNotificationSetting(),
                const SizedBox(height: 16),
                _buildNotificationTypes(),
              ],
            ),
            
            const SizedBox(height: 32),
            
            // Section Synchronisation
            _buildSection(
              'Synchronisation',
              Icons.sync,
              [
                _buildAutoSyncSetting(),
                const SizedBox(height: 16),
                _buildOfflineModeSetting(),
                const SizedBox(height: 16),
                _buildStorageInfo(),
              ],
            ),
            
            const SizedBox(height: 32),
            
            // Section Sécurité
            _buildSection(
              'Sécurité',
              Icons.security,
              [
                _buildSecurityOptions(),
              ],
            ),
            
            const SizedBox(height: 32),
            
            // Section À propos
            _buildSection(
              'À propos',
              Icons.info,
              [
                _buildAboutInfo(),
              ],
            ),
            
            const SizedBox(height: 32),
            
            // Bouton de réinitialisation
            _buildResetButton(),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(String title, IconData icon, List<Widget> children) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: AppColors.shadow,
            offset: const Offset(0, 4),
            blurRadius: 12,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: AppColors.primary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  icon,
                  color: AppColors.primary,
                  size: 20,
                ),
              ),
              const SizedBox(width: 16),
              Text(
                title,
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),
          ...children,
        ],
      ),
    );
  }

  Widget _buildLanguageSetting() {
    return _buildSettingTile(
      'Langue',
      'Choisir la langue de l\'application',
      Icons.language,
      _selectedLanguage,
      () => _showLanguageDialog(),
    );
  }

  Widget _buildThemeSetting() {
    return _buildSwitchTile(
      'Mode sombre',
      'Activer le thème sombre',
      Icons.dark_mode,
      _darkModeEnabled,
      (value) {
        setState(() {
          _darkModeEnabled = value;
        });
        // TODO: Appliquer le thème
      },
    );
  }

  Widget _buildQualitySetting() {
    return _buildSettingTile(
      'Qualité vidéo',
      'Définir la qualité par défaut',
      Icons.high_quality,
      _selectedQuality,
      () => _showQualityDialog(),
    );
  }

  Widget _buildNotificationSetting() {
    return _buildSwitchTile(
      'Notifications',
      'Activer les notifications push',
      Icons.notifications_active,
      _notificationsEnabled,
      (value) {
        setState(() {
          _notificationsEnabled = value;
        });
        // TODO: Configurer les notifications
      },
    );
  }

  Widget _buildNotificationTypes() {
    if (!_notificationsEnabled) return const SizedBox.shrink();
    
    return Column(
      children: [
        _buildSwitchTile(
          'Nouveaux projets',
          'Recevoir des notifications pour les nouveaux projets',
          Icons.folder,
          true,
          (value) {},
        ),
        const SizedBox(height: 8),
        _buildSwitchTile(
          'Mises à jour',
          'Recevoir des notifications pour les mises à jour',
          Icons.system_update,
          true,
          (value) {},
        ),
        const SizedBox(height: 8),
        _buildSwitchTile(
          'Partages',
          'Recevoir des notifications pour les partages',
          Icons.share,
          false,
          (value) {},
        ),
      ],
    );
  }

  Widget _buildAutoSyncSetting() {
    return _buildSwitchTile(
      'Synchronisation automatique',
      'Synchroniser automatiquement vos données',
      Icons.sync,
      _autoSyncEnabled,
      (value) {
        setState(() {
          _autoSyncEnabled = value;
        });
        // TODO: Configurer la synchronisation
      },
    );
  }

  Widget _buildOfflineModeSetting() {
    return _buildSwitchTile(
      'Mode hors ligne',
      'Permettre l\'utilisation sans connexion',
      Icons.offline_bolt,
      _offlineModeEnabled,
      (value) {
        setState(() {
          _offlineModeEnabled = value;
        });
        // TODO: Configurer le mode offline
      },
    );
  }

  Widget _buildStorageInfo() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.background,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: AppColors.border,
          width: 1,
        ),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: AppColors.info.withOpacity(0.1),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(
              Icons.storage,
              color: AppColors.info,
              size: 20,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Stockage',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  '2.4 GB utilisés sur 8 GB',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppColors.textSecondary,
                  ),
                ),
              ],
            ),
          ),
          LinearProgressIndicator(
            value: 0.3,
            backgroundColor: AppColors.border,
            valueColor: AlwaysStoppedAnimation<Color>(AppColors.info),
          ),
        ],
      ),
    );
  }

  Widget _buildSecurityOptions() {
    return Column(
      children: [
        _buildActionTile(
          'Changer le mot de passe',
          'Mettre à jour votre mot de passe',
          Icons.lock,
          AppColors.primary,
          () {
            // TODO: Changer le mot de passe
          },
        ),
        const SizedBox(height: 16),
        _buildActionTile(
          'Authentification à deux facteurs',
          'Ajouter une couche de sécurité',
          Icons.security,
          AppColors.secondary,
          () {
            // TODO: Configurer 2FA
          },
        ),
        const SizedBox(height: 16),
        _buildActionTile(
          'Sessions actives',
          'Gérer vos connexions',
          Icons.devices,
          AppColors.accent,
          () {
            // TODO: Gérer les sessions
          },
        ),
      ],
    );
  }

  Widget _buildAboutInfo() {
    return Column(
      children: [
        _buildInfoTile('Version', '1.0.0'),
        const SizedBox(height: 16),
        _buildInfoTile('Développeur', 'TVCam Team'),
        const SizedBox(height: 16),
        _buildInfoTile('Licence', 'MIT License'),
        const SizedBox(height: 16),
        _buildActionTile(
          'Politique de confidentialité',
          'Lire notre politique de confidentialité',
          Icons.privacy_tip,
          AppColors.info,
          () {
            // TODO: Ouvrir la politique de confidentialité
          },
        ),
        const SizedBox(height: 16),
        _buildActionTile(
          'Conditions d\'utilisation',
          'Lire nos conditions d\'utilisation',
          Icons.description,
          AppColors.info,
          () {
            // TODO: Ouvrir les conditions d'utilisation
          },
        ),
      ],
    );
  }

  Widget _buildSettingTile(String title, String subtitle, IconData icon, String value, VoidCallback onTap) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: AppColors.primary.withOpacity(0.1),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(
          icon,
          color: AppColors.primary,
          size: 20,
        ),
      ),
      title: Text(
        title,
        style: Theme.of(context).textTheme.titleMedium?.copyWith(
          fontWeight: FontWeight.w600,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: AppColors.textSecondary,
        ),
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            value,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppColors.textSecondary,
            ),
          ),
          const SizedBox(width: 8),
          Icon(
            Icons.chevron_right,
            color: AppColors.textTertiary,
            size: 20,
          ),
        ],
      ),
      onTap: onTap,
    );
  }

  Widget _buildSwitchTile(String title, String subtitle, IconData icon, bool value, ValueChanged<bool> onChanged) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: AppColors.primary.withOpacity(0.1),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(
          icon,
          color: AppColors.primary,
          size: 20,
        ),
      ),
      title: Text(
        title,
        style: Theme.of(context).textTheme.titleMedium?.copyWith(
          fontWeight: FontWeight.w600,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: AppColors.textSecondary,
        ),
      ),
      trailing: Switch(
        value: value,
        onChanged: onChanged,
        activeColor: AppColors.primary,
      ),
    );
  }

  Widget _buildActionTile(String title, String subtitle, IconData icon, Color color, VoidCallback onTap) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(
          icon,
          color: color,
          size: 20,
        ),
      ),
      title: Text(
        title,
        style: Theme.of(context).textTheme.titleMedium?.copyWith(
          fontWeight: FontWeight.w600,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: AppColors.textSecondary,
        ),
      ),
      trailing: Icon(
        Icons.chevron_right,
        color: AppColors.textTertiary,
        size: 20,
      ),
      onTap: onTap,
    );
  }

  Widget _buildInfoTile(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppColors.textSecondary,
          ),
        ),
        Text(
          value,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppColors.textPrimary,
            fontWeight: FontWeight.w500,
          ),
        ),
      ],
    );
  }

  Widget _buildResetButton() {
    return SizedBox(
      width: double.infinity,
      height: 56,
      child: OutlinedButton(
        onPressed: () => _showResetDialog(),
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.error,
          side: const BorderSide(color: AppColors.error, width: 2),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
        ),
        child: Text(
          'Réinitialiser les paramètres',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }

  void _showLanguageDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Choisir la langue'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _buildLanguageOption('Français', 'Français'),
            _buildLanguageOption('English', 'Anglais'),
            _buildLanguageOption('Español', 'Espagnol'),
            _buildLanguageOption('Deutsch', 'Allemand'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Annuler'),
          ),
        ],
      ),
    );
  }

  Widget _buildLanguageOption(String value, String label) {
    return RadioListTile<String>(
      title: Text(label),
      value: value,
      groupValue: _selectedLanguage,
      onChanged: (newValue) {
        setState(() {
          _selectedLanguage = newValue!;
        });
        Navigator.of(context).pop();
      },
    );
  }

  void _showQualityDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Qualité vidéo'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _buildQualityOption('Basse qualité', 'Basse qualité'),
            _buildQualityOption('Qualité standard', 'Qualité standard'),
            _buildQualityOption('Haute qualité', 'Haute qualité'),
            _buildQualityOption('Qualité maximale', 'Qualité maximale'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Annuler'),
          ),
        ],
      ),
    );
  }

  Widget _buildQualityOption(String value, String label) {
    return RadioListTile<String>(
      title: Text(label),
      value: value,
      groupValue: _selectedQuality,
      onChanged: (newValue) {
        setState(() {
          _selectedQuality = newValue!;
        });
        Navigator.of(context).pop();
      },
    );
  }

  void _showResetDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Réinitialiser les paramètres'),
        content: const Text(
          'Êtes-vous sûr de vouloir réinitialiser tous les paramètres ? Cette action ne peut pas être annulée.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Annuler'),
          ),
          TextButton(
            onPressed: () {
              // TODO: Réinitialiser les paramètres
              Navigator.of(context).pop();
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Paramètres réinitialisés'),
                  backgroundColor: AppColors.success,
                ),
              );
            },
            style: TextButton.styleFrom(
              foregroundColor: AppColors.error,
            ),
            child: const Text('Réinitialiser'),
          ),
        ],
      ),
    );
  }
}
