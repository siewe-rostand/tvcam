import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';

class AuthTextField extends StatefulWidget {
  final TextEditingController controller;
  final String hintText;
  final IconData prefixIcon;
  final bool isPassword;
  final bool isPasswordVisible;
  final TextInputType? keyboardType;
  final String? Function(String?)? validator;
  final Function(bool)? onPasswordVisibilityChanged;
  final VoidCallback? onTap;
  final bool readOnly;

  const AuthTextField({
    super.key,
    required this.controller,
    required this.hintText,
    required this.prefixIcon,
    this.isPassword = false,
    this.isPasswordVisible = false,
    this.keyboardType,
    this.validator,
    this.onPasswordVisibilityChanged,
    this.onTap,
    this.readOnly = false,
  });

  @override
  State<AuthTextField> createState() => _AuthTextFieldState();
}

class _AuthTextFieldState extends State<AuthTextField> {
  bool _isFocused = false;

  @override
  Widget build(BuildContext context) {
    return Focus(
      onFocusChange: (hasFocus) {
        setState(() {
          _isFocused = hasFocus;
        });
      },
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: _isFocused
                  ? AppColors.primary.withOpacity(0.2)
                  : AppColors.shadowLight,
              offset: const Offset(0, 2),
              blurRadius: _isFocused ? 8 : 4,
            ),
          ],
        ),
        child: TextFormField(
          controller: widget.controller,
          obscureText: widget.isPassword && !widget.isPasswordVisible,
          keyboardType: widget.keyboardType,
          validator: widget.validator,
          readOnly: widget.readOnly,
          onTap: widget.onTap,
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: AppColors.textPrimary,
          ),
          decoration: InputDecoration(
            hintText: widget.hintText,
            hintStyle: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppColors.textTertiary,
            ),
            prefixIcon: Icon(
              widget.prefixIcon,
              color: _isFocused ? AppColors.primary : AppColors.textTertiary,
              size: 24,
            ),
            suffixIcon: widget.isPassword
                ? IconButton(
                    onPressed: () {
                      if (widget.onPasswordVisibilityChanged != null) {
                        widget.onPasswordVisibilityChanged!(!widget.isPasswordVisible);
                      }
                    },
                    icon: Icon(
                      widget.isPasswordVisible
                          ? Icons.visibility_off
                          : Icons.visibility,
                      color: AppColors.textTertiary,
                      size: 24,
                    ),
                  )
                : null,
            filled: true,
            fillColor: Colors.white,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: BorderSide.none,
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: BorderSide(
                color: _isFocused ? AppColors.primary : AppColors.border,
                width: _isFocused ? 2 : 1,
              ),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(
                color: AppColors.primary,
                width: 2,
              ),
            ),
            errorBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(
                color: AppColors.error,
                width: 1,
              ),
            ),
            focusedErrorBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(
                color: AppColors.error,
                width: 2,
              ),
            ),
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 20,
              vertical: 20,
            ),
            errorStyle: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppColors.error,
            ),
          ),
        ),
      ),
    );
  }
}
