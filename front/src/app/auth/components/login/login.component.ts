import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators, } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { InputTextModule } from 'primeng/inputtext';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { InputNumberModule } from 'primeng/inputnumber';
import { StorageService } from '../../../_shared/services/storage.service';
import { RippleModule } from "primeng/ripple";
import { DividerModule } from "primeng/divider";

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
    RippleModule,
    DividerModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private storageService: StorageService
  ) { }

  loginForm: FormGroup = new FormGroup({
    telephone: new FormControl(''),
    password: new FormControl(''),
  });
  submitted: boolean = false;
  isLoading: boolean = false;
  returnUrl: string = '/dashboard';

  ngOnInit(): void {
    // Check if user is already logged in
    if (this.storageService.isTokenValid()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    // Get return URL from route parameters or default to dashboard
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';

    this.loginForm = this.formBuilder.group({
      telephone: [null, [Validators.required,
      Validators.minLength(9),
      Validators.maxLength(9),
      ]
      ],
      password: [
        null,
        [Validators.required,
        Validators.minLength(6),
        Validators.maxLength(16),
        ]
      ],
    });
  }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }
    console.log(this.loginForm.value);
    this.isLoading = true;

    this.authService
      .login(this.loginForm.value.telephone, this.loginForm.value.password)
      .subscribe({
        next: (res) => {
          const user = res.data.user;
          const accessToken = res.data.access_token;
          this.storageService.saveUser(user);
          this.storageService.saveToken(accessToken);
          this.isLoading = false;

          // Redirect to the originally requested URL or dashboard
          this.router.navigate([this.returnUrl]);

          console.log('Login successful, redirecting to:', this.returnUrl);
        },
        error: (err) => {
          console.log(err);
          this.isLoading = false;
          this.submitted = false;
        },
      });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.loginForm.controls;
  }
}
