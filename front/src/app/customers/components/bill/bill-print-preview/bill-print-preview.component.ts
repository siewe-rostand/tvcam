import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DividerModule } from 'primeng/divider';
import { MessageService, ConfirmationService } from 'primeng/api';
import { BillModel } from '../../../model/bill.model';
import { BillPrintService } from '../../../service/bill-print.service';
import { BillUtils } from '../../../../_shared/utils/bill.utils';

@Component({
    selector: 'app-bill-print-preview',
    standalone: true,
    imports: [
        CommonModule,
        ButtonModule,
        CardModule,
        ToastModule,
        ConfirmDialogModule,
        DividerModule
    ],
    providers: [MessageService, ConfirmationService],
    templateUrl: './bill-print-preview.component.html',
    styleUrls: ['./bill-print-preview.component.css']
})
export class BillPrintPreviewComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    selectedBills: BillModel[] = [];
    pages: BillModel[][] = [];
    totalPages: number = 0;
    currentPageIndex: number = 0;
    loading: boolean = true;

    constructor(
        private billPrintService: BillPrintService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadSelectedBills();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private loadSelectedBills(): void {
        this.billPrintService.selectedBills$
            .pipe(takeUntil(this.destroy$))
            .subscribe(bills => {
                this.selectedBills = bills;

                if (bills.length === 0) {
                    this.messageService.add({
                        severity: 'warn',
                        summary: 'Aucune facture',
                        detail: 'Aucune facture sélectionnée pour l\'impression',
                        life: 3000
                    });
                    this.router.navigate(['/receipts']);
                    return;
                }

                this.organizeBillsInPages();
                this.loading = false;
            });
    }

    private organizeBillsInPages(): void {
        this.pages = this.billPrintService.organizeBillsInPages(this.selectedBills);
        this.totalPages = this.pages.length;
        this.currentPageIndex = 0;
    }

    // Navigation entre les pages
    previousPage(): void {
        if (this.currentPageIndex > 0) {
            this.currentPageIndex--;
        }
    }

    nextPage(): void {
        if (this.currentPageIndex < this.totalPages - 1) {
            this.currentPageIndex++;
        }
    }

    goToPage(pageIndex: number): void {
        if (pageIndex >= 0 && pageIndex < this.totalPages) {
            this.currentPageIndex = pageIndex;
        }
    }

    // Actions d'impression
    printAll(): void {
        this.confirmationService.confirm({
            message: `Êtes-vous sûr de vouloir imprimer toutes les ${this.selectedBills.length} factures (${this.totalPages} pages) ?`,
            header: 'Confirmation d\'impression',
            icon: 'pi pi-print',
            acceptLabel: 'Imprimer',
            rejectLabel: 'Annuler',
            accept: () => {
                this.executeScriptPrint();
            }
        });
    }

    printCurrentPage(): void {
        this.confirmationService.confirm({
            message: `Imprimer la page ${this.currentPageIndex + 1} ?`,
            header: 'Impression page courante',
            icon: 'pi pi-print',
            acceptLabel: 'Imprimer',
            rejectLabel: 'Annuler',
            accept: () => {
                this.executePrintCurrentPage();
            }
        });
    }

    private executeScriptPrint(): void {
        // Impression de toutes les pages
        window.print();

        this.messageService.add({
            severity: 'success',
            summary: 'Impression lancée',
            detail: `Impression de ${this.selectedBills.length} factures en cours...`,
            life: 3000
        });
    }

    private executePrintCurrentPage(): void {
        // Pour imprimer une seule page, on peut temporairement cacher les autres
        const allPages = document.querySelectorAll('.print-page');
        allPages.forEach((page, index) => {
            const pageElement = page as HTMLElement;
            if (index !== this.currentPageIndex) {
                pageElement.style.display = 'none';
            }
        });

        window.print();

        // Restaurer l'affichage après impression
        setTimeout(() => {
            allPages.forEach(page => {
                const pageElement = page as HTMLElement;
                pageElement.style.display = 'block';
            });
        }, 1000);

        this.messageService.add({
            severity: 'success',
            summary: 'Impression page',
            detail: `Impression de la page ${this.currentPageIndex + 1} en cours...`,
            life: 3000
        });
    }

    // Actions de navigation
    goBackToBills(): void {
        this.router.navigate(['/receipts']);
    }

    clearSelection(): void {
        this.confirmationService.confirm({
            message: 'Êtes-vous sûr de vouloir vider la sélection ?',
            header: 'Vider la sélection',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Oui',
            rejectLabel: 'Non',
            accept: () => {
                this.billPrintService.clearSelection();
                this.router.navigate(['/receipts']);
            }
        });
    }

    // Utilitaires de formatage (similaires aux composants existants)
    getMonthLabel(monthValue?: string): string {
        return BillUtils.getMonthLabelFr(monthValue);
    }

    formatDate(dateValue?: string): string {
        return BillUtils.formatDateFr(dateValue);
    }

    formatAmount(amount?: number): string {
        return BillUtils.formatAmountFCFA(amount);
    }

    getBillReference(bill: BillModel): string {
        return BillUtils.generateBillReference(bill.id, bill.month, bill.year);
    }

    getDepositDate(bill: BillModel): string {
        return bill.depositDate ? this.formatDate(bill.depositDate) : this.formatDate(new Date().toISOString());
    }

    getCurrentPage(): BillModel[] {
        return this.pages[this.currentPageIndex] || [];
    }

    // Getters pour le template
    get hasMultiplePages(): boolean {
        return this.totalPages > 1;
    }

    get canGoPrevious(): boolean {
        return this.currentPageIndex > 0;
    }

    get canGoNext(): boolean {
        return this.currentPageIndex < this.totalPages - 1;
    }

    get currentPageNumber(): number {
        return this.currentPageIndex + 1;
    }
}
