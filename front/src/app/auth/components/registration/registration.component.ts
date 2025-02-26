import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators,} from '@angular/forms';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {PasswordModule} from 'primeng/password';
import {InputNumberModule} from 'primeng/inputnumber';
import {AuthService} from '../../services/auth.service';
import {CommonModule} from '@angular/common';
import {MessageService} from 'primeng/api';
import {ToastModule} from 'primeng/toast';
import {Router} from '@angular/router';
import {PasswordValidatorService} from '../../services/password.service';

@Component({
  selector: 'app-registration',
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
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css',
  providers: [MessageService],
})
export class RegistrationComponent implements OnInit {
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private messageService: MessageService,
    private route: Router,
    private passwordValidator: PasswordValidatorService
  ) {}
  ngOnInit(): void {
    this.registrationForm = this.formBuilder.group({
      firstname: [null, Validators.required],
      lastname: [null, Validators.required],
      telephone: [null, Validators.required],
      password: [
        null,
        Validators.required,
        this.passwordValidator.passwordValidator(),
      ],
    });
  }

  error = '';
  registrationForm!: FormGroup;

  loading: boolean = false;
  returnUrl!: string;
  submitted: boolean = false;

  get f() {
    return this.registrationForm.controls;
  }
  onSubmit() {
    console.log(this.registrationForm.value);
    this.submitted = true;
    this.loading = true;
    const data = this.registrationForm.value;
    this.authService.signup(data).subscribe({
      next: (res) => {
        console.log(res);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'nouvel utilisateur enregistrer avec succès',
        });
        this.route.navigate(['/login']).then(r => r);
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
