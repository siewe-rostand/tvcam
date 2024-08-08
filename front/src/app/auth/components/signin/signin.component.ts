import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { InputNumberModule } from 'primeng/inputnumber';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { Router } from '@angular/router';
import { PasswordValidatorService } from '../../services/password.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    PasswordModule,
    InputTextModule,
    InputNumberModule,
    CommonModule,
    ToastModule,
  ],
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css',
  providers: [MessageService],
})
export class SigninComponent implements OnInit {
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private messageService: MessageService,
    private route: Router,
    private passwordValidator: PasswordValidatorService
  ) {}
  ngOnInit(): void {
    this.signinForm = this.formBuilder.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      telephone: ['', Validators.required],
      password: [
        '',
        Validators.required,
        this.passwordValidator.passwordValidator(),
      ],
    });
  }

  error = '';
  signinForm!: FormGroup;

  loading: boolean = false;
  returnUrl!: string;
  submitted: boolean = false;

  get f() {
    return this.signinForm.controls;
  }
  onSubmit() {
    console.log(this.signinForm.value);
    this.submitted = true;
    this.loading = true;
    const data = this.signinForm.value;
    this.authService.signin(data).subscribe({
      next: (res) => {
        console.log(res);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'nouvel utilisateur enregistrer avec succès',
        });
        this.route.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: "Une erreur est survenue. Veuillez contacter l'admin",
        });
      },
    });
  }
}
