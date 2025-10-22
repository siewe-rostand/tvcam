package com.siewe_rostand.tvcam.Bills.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siewe_rostand.tvcam.Customers.model.Customers;
import com.siewe_rostand.tvcam.Payment.model.Payments;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentStatus;
import com.siewe_rostand.tvcam.shared.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "bills")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Schema(
    name = "Bills",
    description = "Entité représentant une facture mensuelle d'un client dans le système TV-CAM"
)
public class Bills extends BaseEntity {

  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "bills", fetch = FetchType.LAZY, orphanRemoval = true)
  @Schema(hidden = true, description = "Liste des paiements associés à cette facture")
  List<Payments> payments;

  @Id
  @SequenceGenerator(name = "bills_seq", sequenceName = "bills_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bills_seq")
  @Column(name = "id")
  @Schema(
      description = "Identifiant unique de la facture",
      example = "12345",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  private Long billId;

  @NotNull(message = "need to specify the month of the bill")
  @Schema(
      description = "Mois de la facture (1-12)",
      example = "10",
      minimum = "1",
      maximum = "12",
      required = true
  )
  private Integer month;

  @NotNull(message = "Year is required")
  @Schema(
      description = "Année de la facture",
      example = "2025",
      minimum = "2020",
      required = true
  )
  private Integer year;

  @Column(name = "deposit_date")
  @Schema(
      description = "Date de dépôt/génération de la facture au format dd/MM/yyyy",
      example = "21/10/2025",
      pattern = "dd/MM/yyyy"
  )
  private String depositDate;

  @Schema(
      description = "Date limite de paiement au format dd/MM/yyyy",
      example = "31/10/2025",
      pattern = "dd/MM/yyyy"
  )
  private String deadline;

  @Schema(
      description = "Montant de la dette antérieure reportée sur cette facture (cumul des impayés précédents)",
      example = "15000.00",
      minimum = "0"
  )
  private BigDecimal debt;

  @Schema(
      description = "Montant des pénalités appliquées en cas de retard de paiement",
      example = "500",
      minimum = "0"
  )
  private Integer penalties;

  @NotNull(message = "bill amount must not be null")
  @Schema(
      description = "Montant déjà payé par le client pour cette facture",
      example = "10000.00",
      minimum = "0",
      required = true
  )
  private BigDecimal paidAmount;

  @Column(name = "net_to_pay")
  @Schema(
      description = "Montant net total à payer (dette antérieure + montant mensuel)",
      example = "17000.00",
      minimum = "0"
  )
  private BigDecimal netToPay;

  @Schema(
      description = "Montant mensuel de l'abonnement TV-CAM",
      example = "2000.00",
      minimum = "0"
  )
  private BigDecimal monthlyPayment;

  @Schema(
      description = "Observations ou remarques concernant la facture",
      example = "Vous serez suspendu si vous n'avez pas payé après la date limite de paiement.",
      maxLength = 500
  )
  private String observation;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(
      description = "Statut de paiement de la facture",
      example = "UNPAID",
      allowableValues = {"PAID", "UNPAID", "PARTIALLY_PAID", "OVERDUE"}
  )
  private PaymentStatus paymentStatus;

  @Schema(
      description = "Indique si c'est la facture de la période courante",
      example = "true"
  )
  private boolean currentPeriodBill;

  @ManyToOne()
  @JoinColumn(name = "customer_id")
  @Schema(description = "Client propriétaire de cette facture")
  private Customers customers;

}
