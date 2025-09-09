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
import { switchMap } from 'rxjs';
import { LoginResponse } from '../../model/auth-response.model';
import { ApiResponse } from '../../../_shared/model/api-response';
import { UserModel } from '../../../user/model/user.model';

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
    if (this.storageService.isTokenValid()) {
      this.router.navigate(['/dashboard']).then(() => true);
      return;
    }

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
      .pipe(
        switchMap((res: ApiResponse<LoginResponse>) => {
          if (res.data.accessToken)
            this.storageService.saveToken(res.data.accessToken);
          return this.authService.getUserInfo(res.data.accessToken);
        })
      )
      .subscribe({
        next: (res: UserModel) => {
          this.authService.handleLoginSuccess(res);

          this.isLoading = false;
        },
        error: (err) => {
          console.log('Login error:', err);
          this.isLoading = false;
          this.submitted = false;
        },
      });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.loginForm.controls;
  }
}
