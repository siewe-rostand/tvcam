import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/constants/app_colors.dart';
import '../widgets/feature_card.dart';
import '../widgets/quick_action_button.dart';
import '../widgets/recent_activity_card.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage>
    with TickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    ));
    
    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.3),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeOutCubic,
    ));
    
    _animationController.forward();
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: SlideTransition(
            position: _slideAnimation,
            child: CustomScrollView(
              slivers: [
                // En-tête personnalisé
                _buildSliverAppBar(),
                
                // Contenu principal
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Section de bienvenue
                        _buildWelcomeSection(),
                        
                        const SizedBox(height: 32),
                        
                        // Actions rapides
                        _buildQuickActions(),
                        
                        const SizedBox(height: 32),
                        
                        // Fonctionnalités principales
                        _buildMainFeatures(),
                        
                        const SizedBox(height: 32),
                        
                        // Activité récente
                        _buildRecentActivity(),
                        
                        const SizedBox(height: 32),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSliverAppBar() {
    return SliverAppBar(
      expandedHeight: 120,
      floating: false,
      pinned: true,
      backgroundColor: AppColors.surface,
      elevation: 0,
      flexibleSpace: FlexibleSpaceBar(
        background: Container(
          decoration: const BoxDecoration(
            gradient: AppColors.primaryGradient,
          ),
          child: SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 24,
                    backgroundColor: Colors.white.withOpacity(0.2),
                    child: const Icon(
                      Icons.person,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'Bonjour !',
                          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                            color: Colors.white,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        Text(
                          'Bienvenue sur TVCam',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: Colors.white.withOpacity(0.9),
                          ),
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    onPressed: () {
                      // TODO: Ouvrir les notifications
                    },
                    icon: const Icon(
                      Icons.notifications_outlined,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildWelcomeSection() {
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
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.primary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  Icons.camera_alt,
                  color: AppColors.primary,
                  size: 24,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Prêt à capturer ?',
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text(
                      'Commencez à utiliser TVCam pour vos projets vidéo',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppColors.textSecondary,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildQuickActions() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Actions rapides',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: QuickActionButton(
                icon: Icons.camera_alt,
                label: 'Nouvelle vidéo',
                onTap: () {
                  // TODO: Ouvrir la caméra
                },
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: QuickActionButton(
                icon: Icons.upload_file,
                label: 'Importer',
                onTap: () {
                  // TODO: Importer des fichiers
                },
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: QuickActionButton(
                icon: Icons.edit,
                label: 'Éditer',
                onTap: () {
                  // TODO: Ouvrir l'éditeur
                },
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: QuickActionButton(
                icon: Icons.share,
                label: 'Partager',
                onTap: () {
                  // TODO: Partager des projets
                },
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildMainFeatures() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Fonctionnalités',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          childAspectRatio: 1.2,
          children: [
            FeatureCard(
              icon: Icons.camera_alt,
              title: 'Enregistrement',
              description: 'Capturez des vidéos de haute qualité',
              color: AppColors.primary,
              onTap: () {
                // TODO: Ouvrir l'enregistrement
              },
            ),
            FeatureCard(
              icon: Icons.edit,
              title: 'Édition',
              description: 'Modifiez et améliorez vos vidéos',
              color: AppColors.secondary,
              onTap: () {
                // TODO: Ouvrir l'éditeur
              },
            ),
            FeatureCard(
              icon: Icons.cloud_upload,
              title: 'Synchronisation',
              description: 'Sauvegardez et partagez vos projets',
              color: AppColors.accent,
              onTap: () {
                // TODO: Ouvrir la synchronisation
              },
            ),
            FeatureCard(
              icon: Icons.settings,
              title: 'Paramètres',
              description: 'Personnalisez votre expérience',
              color: AppColors.info,
              onTap: () {
                // TODO: Ouvrir les paramètres
              },
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildRecentActivity() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Activité récente',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            TextButton(
              onPressed: () {
                // TODO: Voir toute l'activité
              },
              child: const Text('Voir tout'),
            ),
          ],
        ),
        const SizedBox(height: 16),
        const RecentActivityCard(
          title: 'Projet "Vacances d\'été"',
          subtitle: 'Modifié il y a 2 heures',
          icon: Icons.edit,
          color: AppColors.secondary,
        ),
        const SizedBox(height: 12),
        const RecentActivityCard(
          title: 'Nouvelle vidéo importée',
          subtitle: 'Il y a 1 jour',
          icon: Icons.upload_file,
          color: AppColors.primary,
        ),
        const SizedBox(height: 12),
        const RecentActivityCard(
          title: 'Projet partagé avec Jean',
          subtitle: 'Il y a 2 jours',
          icon: Icons.share,
          color: AppColors.accent,
        ),
      ],
    );
  }
}
