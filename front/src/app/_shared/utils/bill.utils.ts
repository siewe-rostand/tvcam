/**
 * Utility functions for bill formatting and date handling
 */

export class BillUtils {

    /**
     * Format date to French format DD/MM/YYYY
     */
    static formatDateFr(date: string | Date | undefined): string {
        if (!date) return '';

        try {
            const dateObj = typeof date === 'string' ? new Date(date) : date;

            const day = dateObj.getDate().toString().padStart(2, '0');
            const month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
            const year = dateObj.getFullYear();

            return `${day}/${month}/${year}`;
        } catch (error) {
            console.error('Error formatting date:', error);
            return typeof date === 'string' ? date : '';
        }
    }

    /**
     * Get current date in DD/MM/YYYY format for deposit date
     */
    static getCurrentDateFr(): string {
        return this.formatDateFr(new Date());
    }

    /**
     * Calculate deadline date (usually 1 month from deposit date)
     */
    static calculateDeadline(depositDate?: string | Date, daysToAdd: number = 30): string {
        const baseDate = depositDate ? new Date(depositDate) : new Date();
        const deadline = new Date(baseDate);
        deadline.setDate(deadline.getDate() + daysToAdd);

        return this.formatDateFr(deadline);
    }

    /**
     * Convert month string to French label
     */
    static getMonthLabelFr(monthValue?: string): string {
        const months: { [key: string]: string } = {
            'january': 'Janvier',
            'february': 'Février',
            'march': 'Mars',
            'april': 'Avril',
            'may': 'Mai',
            'june': 'Juin',
            'july': 'Juillet',
            'august': 'Août',
            'september': 'Septembre',
            'october': 'Octobre',
            'november': 'Novembre',
            'december': 'Décembre'
        };

        if (!monthValue) return '';
        return months[monthValue.toLowerCase()] || monthValue;
    }

    /**
     * Format amount with FCFA currency
     */
    static formatAmountFCFA(amount?: number): string {
        if (amount === undefined || amount === null) return '0 FCFA';

        return new Intl.NumberFormat('fr-FR', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount) + ' FCFA';
    }

    /**
     * Generate bill reference number
     */
    static generateBillReference(billId?: number, month?: string, year?: string): string {
        if (!billId || !month || !year) return '';

        const monthNum = this.getMonthNumber(month);
        return `FAC-${year}${monthNum.toString().padStart(2, '0')}-${billId.toString().padStart(4, '0')}`;
    }

    /**
     * Get month number from month string
     */
    private static getMonthNumber(monthValue: string): number {
        const months: { [key: string]: number } = {
            'january': 1, 'february': 2, 'march': 3, 'april': 4,
            'may': 5, 'june': 6, 'july': 7, 'august': 8,
            'september': 9, 'october': 10, 'november': 11, 'december': 12
        };

        return months[monthValue.toLowerCase()] || 1;
    }

    /**
     * Validate if bill generation is allowed for the current month
     */
    static canGenerateBillForMonth(lastBillMonth?: string, lastBillYear?: string): boolean {
        if (!lastBillMonth || !lastBillYear) return true;

        const currentDate = new Date();
        const currentMonth = this.getCurrentMonthKey();
        const currentYear = currentDate.getFullYear().toString();

        // Allow generation if it's a different month or year
        return !(lastBillMonth === currentMonth && lastBillYear === currentYear);
    }

    /**
     * Get current month key (january, february, etc.)
     */
    static getCurrentMonthKey(): string {
        const monthKeys = [
            'january', 'february', 'march', 'april', 'may', 'june',
            'july', 'august', 'september', 'october', 'november', 'december'
        ];

        const currentMonth = new Date().getMonth();
        return monthKeys[currentMonth];
    }

    /**
     * Get default deposit date (first day of current month)
     */
    static getDefaultDepositDate(): string {
        const now = new Date();
        const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
        return this.formatDateFr(firstDayOfMonth);
    }

    /**
     * Get default deadline date (last day of current month)
     */
    static getDefaultDeadlineDate(): string {
        const now = new Date();
        const lastDayOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        return this.formatDateFr(lastDayOfMonth);
    }
}
