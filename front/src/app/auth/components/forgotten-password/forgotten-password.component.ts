import {Component, OnInit} from '@angular/core';
import {PanelModule} from "primeng/panel";
import {CardModule} from "primeng/card";
import {ButtonDirective} from "primeng/button";
import {DividerModule} from "primeng/divider";
import {InputNumberModule} from "primeng/inputnumber";
import {PasswordModule} from "primeng/password";
import {AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Ripple} from "primeng/ripple";
import {Router, RouterLink} from "@angular/router";
import {CommonModule} from "@angular/common";
import {AuthService} from "../../services/auth.service";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-forgotten-password',
  standalone: true,
  imports: [
    PanelModule,
    CardModule,
    ButtonDirective,
    DividerModule,
    InputNumberModule,
    PasswordModule,
    ReactiveFormsModule,
    Ripple,
    RouterLink,
    CommonModule,
  ],
  templateUrl: './forgotten-password.component.html',
  styleUrl: './forgotten-password.component.css',
  providers: [MessageService]
})
export class ForgottenPasswordComponent implements OnInit {
  constructor(private authService: AuthService, private formBuilder: FormBuilder,
              private router: Router, private messageService: MessageService) {
  }


  ngOnInit(): void {
    this.form = this.formBuilder.group(
      {
        telephone: new FormControl('', Validators.required),
        newPassword: new FormControl('', Validators.required),
      }
    )
  }

  form: FormGroup = new FormGroup({
    telephone: new FormControl(''),
    newPassword: new FormControl(''),
  });
  submitted: boolean = false;
  isLoading: boolean = false;


  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
  }

  onSubmit() {
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }
    this.isLoading = true;

    this.authService
      .changePassword(this.form.value.telephone.toString(), this.form.value.newPassword)
      .subscribe({
        next: (res) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Mot de passe mis à jour avec succès',
          })
          this.router.navigate(['/login']).then(r => true);
          // console.log(res.data.user);
        },
        error: (err) => {
          this.isLoading = false;
          this.submitted = false;
        },
      });
  }


}
