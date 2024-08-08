import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { InputTextModule } from 'primeng/inputtext';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthRequestModel } from '../../model/auth-request.model';
import { CommonModule } from '@angular/common';
import { InputNumberModule } from 'primeng/inputnumber';
import { PasswordValidatorService } from '../../services/password.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    PasswordModule,
    InputTextModule,
    InputNumberModule,
    RouterLink,
    CommonModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private passwordValidator: PasswordValidatorService,
    private storageService: StorageService
  ) {}

  loginForm!: FormGroup;
  authReq!: AuthRequestModel;
  submitted: boolean = false;
  isLoading: boolean = false;
  isLoggedIn = false;
  isLoginFailed = false;

  ngOnInit(): void {
    // if (this.storageService.isLoggedIn()) {
    //   this.router.navigate(['users']);
    // }

    this.loginForm = this.formBuilder.group({
      telephone: ['', Validators.required],
      password: [
        '',
        Validators.required,
        this.passwordValidator.passwordValidator(),
      ],
    });
  }

  onSubmit() {
    this.submitted = true;
    this.isLoading = true;

    if (this.loginForm.valid) {
      console.log(this.loginForm.value);

      this.authService
        .login(this.loginForm.value.telephone, this.loginForm.value.password)
        .subscribe({
          next: (res) => {
            const user = res.data.user;
            const accessToken = res.data.access_token;
            this.storageService.saveUser(user);
            this.storageService.saveToken(accessToken);
            this.isLoading = false;
            this.router.navigate(['users']);
            // console.log(res.data.user);
          },
          error: (err) => {
            console.log(err);
            this.isLoading = false;
          },
        });
    }
  }

  get f() {
    return this.loginForm.controls;
  }
}
