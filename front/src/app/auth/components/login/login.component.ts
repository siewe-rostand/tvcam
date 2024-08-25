import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators,} from '@angular/forms';
import {ButtonModule} from 'primeng/button';
import {PasswordModule} from 'primeng/password';
import {InputTextModule} from 'primeng/inputtext';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequestModel} from '../../model/auth-request.model';
import {CommonModule} from '@angular/common';
import {InputNumberModule} from 'primeng/inputnumber';
import {PasswordValidatorService} from '../../services/password.service';
import {StorageService} from '../../services/storage.service';
import {RippleModule} from "primeng/ripple";
import {DividerModule} from "primeng/divider";

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
    private storageService: StorageService
  ) {}

  loginForm: FormGroup = new FormGroup({
    telephone: new FormControl(''),
    password: new FormControl(''),
  });
  submitted: boolean = false;
  isLoading: boolean = false;

  ngOnInit(): void {
    // if (this.storageService.isLoggedIn()) {
    //   this.router.navigate(['users']);
    // }

    this.loginForm = this.formBuilder.group({
      telephone: ['', [Validators.required,
        Validators.minLength(9),
        Validators.maxLength(9),
      ]
      ],
      password: [
        '',
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
          this.router.navigate(['users']);
          // console.log(res.data.user);
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
