package com.clxns.app.data.model

import com.clxns.app.data.model.cases.Fi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyPlanModel(

    @Json(name = "total")
    val total: Int? = null,

    @Json(name = "data")
    val data: List<MyPlanDataItem>?,

    @Json(name = "title")
    val title: String? = null,

    @Json(name = "error")
    val error: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class MyPlanDataItem(

    @Json(name = "createdAt")
    val createdAt: String? = null,

    @Json(name = "fosId")
    val fosId: Int? = null,

    @Json(name = "id")
    val id: Int? = null,

    @Json(name = "planDate")
    val planDate: String? = null,

    @Json(name = "lead")
    val lead: Lead? = null,

    @Json(name = "leadId")
    val leadId: Int? = null,

    @Json(name = "updatedAt")
    val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class Lead(

    @Json(name = "locationVerified")
    val locationVerified: String? = null,

    @Json(name = "amountOutstanding")
    val amountOutstanding: Int? = null,

    @Json(name = "amountCollected")
    val amountCollected: Int? = null,

    @Json(name = "applicantCibilScore")
    val applicantCibilScore: Int? = null,

    @Json(name = "emiDueAmount")
    val emiDueAmount: Int? = null,

    @Json(name = "coApplicantName")
    val coApplicantName: String? = null,

    @Json(name = "cityId")
    val cityId: Any? = null,

    @Json(name = "emiAmount")
    val emiAmount: Int? = null,

    @Json(name = "fieldVisitStatus")
    val fieldVisitStatus: String? = null,

    @Json(name = "penaltyAmount")
    val penaltyAmount: Int? = null,

    @Json(name = "band")
    val band: String? = null,

    @Json(name = "refContactNumber_3")
    val refContactNumber3: String? = null,

    @Json(name = "refContactNumber_2")
    val refContactNumber2: String? = null,

    @Json(name = "coApplicantContactNumber")
    val coApplicantContactNumber: String? = null,

    @Json(name = "FIId")
    val fIId: Int? = null,

    @Json(name = "refContactNumber_1")
    val refContactNumber1: String? = null,

    @Json(name = "applicantPincode")
    val applicantPincode: Int? = null,

    @Json(name = "coApplicantCity")
    val coApplicantCity: String? = null,

    @Json(name = "dispositionId")
    val dispositionId: Any? = null,

    @Json(name = "applicantAltAddressType")
    val applicantAltAddressType: String? = null,

    @Json(name = "phone")
    val phone: String? = null,

    @Json(name = "applicantAltCity")
    val applicantAltCity: String? = null,

    @Json(name = "collectionStatus")
    val collectionStatus: Any? = null,

    @Json(name = "poolId")
    val poolId: Int? = null,

    @Json(name = "applicantAlternateMobile_1")
    val applicantAlternateMobile1: String? = null,

    @Json(name = "applicantAlternateMobile_2")
    val applicantAlternateMobile2: String? = null,

    @Json(name = "applicantAlternateMobile_3")
    val applicantAlternateMobile3: String? = null,

    @Json(name = "applicantAltPincode")
    val applicantAltPincode: String? = null,

    @Json(name = "closeStatus")
    val closeStatus: String? = null,

    @Json(name = "disbursementType")
    val disbursementType: String? = null,

    @Json(name = "principalOutstandingAmount")
    val principalOutstandingAmount: Int? = null,

    @Json(name = "daysDue")
    val daysDue: Int? = null,

    @Json(name = "isDeleted")
    val isDeleted: Int? = null,

    @Json(name = "fosId")
    val fosId: Int? = null,

    @Json(name = "totalLoanAmount")
    val totalLoanAmount: Int? = null,

    @Json(name = "pos")
    val pos: Int? = null,

    @Json(name = "applicantAltAddress")
    val applicantAltAddress: String? = null,

    @Json(name = "callStatus")
    val callStatus: String? = null,

    @Json(name = "applicantDob")
    val applicantDob: Any? = null,

    @Json(name = "fosAssignedDate")
    val fosAssignedDate: String? = null,

    @Json(name = "reminderDate")
    val reminderDate: Any? = null,

    @Json(name = "refName_3")
    val refName3: String? = null,

    @Json(name = "email")
    val email: String? = null,

    @Json(name = "refName_2")
    val refName2: String? = null,

    @Json(name = "key")
    val key: String? = null,

    @Json(name = "refName_1")
    val refName1: String? = null,

    @Json(name = "allocationDate")
    val allocationDate: String? = null,

    @Json(name = "address")
    val address: String? = null,

    @Json(name = "ODValue")
    val oDValue: Int? = null,

    @Json(name = "telecallerAssignedDate")
    val telecallerAssignedDate: Any? = null,

    @Json(name = "engineNumber")
    val engineNumber: String? = null,

    @Json(name = "applicantAddress")
    val applicantAddress: String? = null,

    @Json(name = "dateOfDefault")
    val dateOfDefault: String? = null,

    @Json(name = "chequeDate")
    val chequeDate: Any? = null,

    @Json(name = "coApplicantEmail")
    val coApplicantEmail: String? = null,

    @Json(name = "transactionType")
    val transactionType: String? = null,

    @Json(name = "emiStartDate")
    val emiStartDate: String? = null,

    @Json(name = "coApplicantPincode")
    val coApplicantPincode: String? = null,

    @Json(name = "penalAmount")
    val penalAmount: Int? = null,

    @Json(name = "coApplicantAddressType")
    val coApplicantAddressType: String? = null,

    @Json(name = "transactionNo")
    val transactionNo: String? = null,

    @Json(name = "businessName")
    val businessName: String? = null,

    @Json(name = "applicantCity")
    val applicantCity: String? = null,

    @Json(name = "makeAndModel")
    val makeAndModel: String? = null,

    @Json(name = "coApplicantDob")
    val coApplicantDob: String? = null,

    @Json(name = "refRelationWithApplicant_2")
    val refRelationWithApplicant2: String? = null,

    @Json(name = "refRelationWithApplicant_3")
    val refRelationWithApplicant3: String? = null,

    @Json(name = "createdAt")
    val createdAt: String? = null,

    @Json(name = "loanDisbursementDate")
    val loanDisbursementDate: String? = null,

    @Json(name = "intendToPay")
    val intendToPay: String? = null,

    @Json(name = "updatedAt")
    val updatedAt: String? = null,

    @Json(name = "ODInt")
    val oDInt: Int? = null,

    @Json(name = "chequeNo")
    val chequeNo: String? = null,

    @Json(name = "interestDueAmount")
    val interestDueAmount: Int? = null,

    @Json(name = "FOSAssigned")
    val fOSAssigned: Any? = null,

    @Json(name = "refRelationWithApplicant_1")
    val refRelationWithApplicant1: String? = null,

    @Json(name = "DPDBucket")
    val dPDBucket: String? = null,

    @Json(name = "applicantState")
    val applicantState: String? = null,

    @Json(name = "chassisNumber")
    val chassisNumber: String? = null,

    @Json(name = "loanAccountNo")
    val loanAccountNo: Int? = null,

    @Json(name = "srNo")
    val srNo: Int? = null,

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "applicantPanNumber")
    val applicantPanNumber: Int? = null,

    @Json(name = "subDispositionId")
    val subDispositionId: Any? = null,

    @Json(name = "telecallerId")
    val telecallerId: Int? = null,

    @Json(name = "applicantAddressType")
    val applicantAddressType: String? = null,

    @Json(name = "agentId")
    val agentId: Any? = null,

    @Json(name = "loanType")
    val loanType: String? = null,

    @Json(name = "coApplicantState")
    val coApplicantState: String? = null,

    @Json(name = "imageVerified")
    val imageVerified: String? = null,

    @Json(name = "tenureFinished")
    val tenureFinished: Boolean? = null,

    @Json(name = "expiryDate")
    val expiryDate: String? = null,

    @Json(name = "coApplicantAddress")
    val coApplicantAddress: String? = null,

    @Json(name = "allocationDpdBucket")
    val allocationDpdBucket: String? = null,

    @Json(name = "disbursementDate")
    val disbursementDate: String? = null,

    @Json(name = "allocationDpd")
    val allocationDpd: Int? = null,

    @Json(name = "paymentStatus")
    val paymentStatus: String? = null,

    @Json(name = "applicantAltState")
    val applicantAltState: String? = null,

    @Json(name = "productTypeId")
    val productTypeId: Int? = null,

    @Json(name = "amountDue")
    val amountDue: Int? = null,

    @Json(name = "vehicleRegistrationNumber")
    val vehicleRegistrationNumber: String? = null,

    @Json(name = "telecallerAssigned")
    val telecallerAssigned: Any? = null,

    @Json(name = "abilityToPay")
    val abilityToPay: String? = null,

    @Json(name = "totalDueAmount")
    val totalDueAmount: Int? = null,

    @Json(name = "teamId")
    val teamId: Int? = null,

    @Json(name = "loanMaturityDate")
    val loanMaturityDate: Any? = null,

    @Json(name = "location")
    val location: String? = null,

    @Json(name = "chequeBank")
    val chequeBank: String? = null,
    @Json(name = "fi")
    val fiData: Fi? = null
)
