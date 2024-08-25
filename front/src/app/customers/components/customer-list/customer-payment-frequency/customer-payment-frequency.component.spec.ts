import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerPaymentFrequencyComponent } from './customer-payment-frequency.component';

describe('CustomerPaymentFrequencyComponent', () => {
  let component: CustomerPaymentFrequencyComponent;
  let fixture: ComponentFixture<CustomerPaymentFrequencyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerPaymentFrequencyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerPaymentFrequencyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
