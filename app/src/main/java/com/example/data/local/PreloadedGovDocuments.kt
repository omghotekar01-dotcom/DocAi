package com.example.data.local

object PreloadedGovDocuments {
    fun getInitialDocuments(): List<DocumentEntity> {
        return listOf(
            DocumentEntity(
                id = 1,
                circularNumber = "MeitY/eGov/2024/34",
                title = "National Cloud First & Data Security Mandate for Public Systems (2024)",
                department = "Ministry of Electronics & Information Technology",
                issueDate = "2024-03-15",
                category = "Policy Directive",
                classification = "PUBLIC",
                fullText = """
                GOVERNMENT OF INDIA
                MINISTRY OF ELECTRONICS & INFORMATION TECHNOLOGY
                NOTIFICATION No. MeitY/eGov/2024/34
                Date: 15th March 2024

                SUBJECT: MANDATE FOR CLOUD-FIRST COMPUTING AND CITIZEN DATA RESIDENCY IN PUBLIC IT INFRASTRUCTURE

                1. SCOPE AND APPLICABILITY
                1.1 This policy directive shall apply to all Central Government Ministries, attached departments, subordinate offices, and statutory commissions.
                1.2 All existing on-premise compute infrastructure shall prepare a staged migration roadmap to empaneled cloud service offerings within twelve (12) calendar months from the date of issue.

                2. DATA SOVEREIGNTY AND RESIDENCY PROVISIONS
                2.1 Sovereign Territory: All primary citizen personal identifiers, biometric repositories, and tax registry records must reside exclusively within the geographic territorial boundaries of the country.
                2.2 Cloud Certification: Government entities are prohibited from onboarding non-empaneled vendors. Cloud environments must possess valid Tier-3 or equivalent availability certification.
                2.3 Cross-Border Mirroring: Mirroring, disaster recovery replication, or cold storage backups located outside sovereign jurisdiction are strictly prohibited.

                3. FINANCIAL CAP AND PROCUREMENT THRESHOLDS
                3.1 Capital Expenditure Clearances: Any new on-premise physical server hardware procurement exceeding INR 5.00 Crore (Rupees Five Crores) requires prior concurrence from the MeitY Technical Evaluation Committee.
                3.2 Software as a Service (SaaS): Preference shall be given to G-Cloud SaaS solutions where annual licensing per user does not exceed the approved rate schedule.

                4. ENCRYPTION STANDARDS AND ACCESS CONTROLS
                4.1 Cipher Strength: Data at rest stored in relational and object storage buckets must be protected with Advanced Encryption Standard 128-bit (AES-128) key management.
                4.2 Data in Transit: Web interfaces and internal APIs must enforce Transport Layer Security version 1.2 (TLS 1.2) or higher.
                4.3 Access Audit: Access logs shall be logged with user ID and timestamp and retained for a minimum period of two (2) years.

                5. CYBER INCIDENT REPORTING TIMELINES
                5.1 Disclosure Window: In the event of an unauthorized intrusion, database leak, or denial-of-service event, the designated Chief Information Security Officer (CISO) shall submit an incident report to CERT-In within twenty-four (24) hours of confirmation.
                5.2 Interim Mitigation: An initial containment brief must follow within forty-eight (48) hours of the initial incident notice.

                6. COMPLIANCE OBLIGATIONS AND PENALTIES
                6.1 Non-Compliance Sanctions: Failure to adhere to provisions outlined in Section 2 and Section 5 shall result in disciplinary proceedings under Central Civil Services (Conduct) Rules.
                6.2 Grant Withholding: MeitY reserves the power to freeze subsequent fiscal year IT digitization disbursements for defaulting bodies.
                """.trimIndent(),
                summary = "2024 National Cloud Mandate establishing Tier-3 cloud adoption, AES-128 encryption, 24-hour CERT-In incident disclosure, and INR 5 Crore procurement ceiling.",
                sha256Hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                isEncrypted = false,
                syncStatus = "SYNCED",
                chunkCount = 6
            ),
            DocumentEntity(
                id = 2,
                circularNumber = "MeitY/eGov/2025/12",
                title = "Revised Cloud First, Edge AI & Zero-Trust Governance Mandate (2025)",
                department = "Ministry of Electronics & Information Technology",
                issueDate = "2025-02-10",
                category = "Policy Directive",
                classification = "PUBLIC",
                fullText = """
                GOVERNMENT OF INDIA
                MINISTRY OF ELECTRONICS & INFORMATION TECHNOLOGY
                NOTIFICATION No. MeitY/eGov/2025/12
                Date: 10th February 2025

                SUBJECT: COMPREHENSIVE REVISION OF CLOUD-FIRST, EDGE-AI COMPUTING, AND ZERO-TRUST GOVERNANCE MANDATE (SUPERSEDING NOTIFICATION 34/2024)

                1. EXPANDED SCOPE AND APPLICABILITY
                1.1 Extended Jurisdiction: This revised mandate supersedes Notification 34/2024. Applicability is expanded to include all Central Ministries, Autonomous Bodies, Central Public Sector Undertakings (CPSUs), and Smart City Special Purpose Vehicles (SPVs).
                1.2 Transition Deadline: All covered organizations must achieve complete compliance and zero-trust perimeter verification within six (6) months of this notification.

                2. SOVEREIGN EDGE COMPUTING AND MULTI-CLOUD ARCHITECTURE
                2.1 Multi-Cloud Redundancy: To avoid single-vendor lock-in, critical mission-mode citizen services must operate with active-active redundancy across at least two distinct Tier-4 certified cloud platforms.
                2.2 Edge AI Nodes: Sensitive document analysis, OCR ingestion, and local vector RAG search must be processed on verified local edge appliances without routing raw citizen records to public cloud endpoints.
                2.3 Sovereign Boundary Guarantee: Complete data lifecycle—including model fine-tuning checkpoints and vector embeddings—must reside inside sovereign data centers.

                3. REVISED FINANCIAL CEILINGS AND OPEN ECOSYSTEMS
                3.1 Lowered Procurement Ceiling: The threshold requiring MeitY technical clearance for hardware compute is lowered to INR 2.50 Crore (Rupees Two Crores Fifty Lakhs) to curb redundant hardware purchases.
                3.2 Sovereign Open-Source Preference: Departments must evaluate open-source foundation models (such as Gemma on-device architectures) prior to proprietary subscription approvals.

                4. POST-QUANTUM ENCRYPTION AND ZERO-TRUST ENFORCEMENT
                4.1 Post-Quantum Ready Rest Cipher: Data at rest must now be encrypted using AES-256 GCM with hardware security module (HSM) backed master keys.
                4.2 Transit Security Upgrade: Enforced TLS 1.3 with mandatory mutual authentication (mTLS) for all inter-departmental API exchanges. Legacy TLS 1.2 is deprecated.
                4.3 Tamper-Evident Audit Trails: Immutable cryptographic audit trails must be retained for seven (7) years with SHA-256 hash chaining.

                5. RAPID CYBER INCIDENT DISCLOSURE (CRITICAL TIMELINE ACCELERATION)
                5.1 Urgent Disclosure Window: Incident notification window to CERT-In and the National Cyber Security Coordinator is accelerated to six (6) hours (tightened from 24 hours under the 2024 circular).
                5.2 Public Vulnerability Disclosure: If citizen personal identifiable information (PII) is compromised, an anonymized public notice must be published within seventy-two (72) hours.

                6. STRICT ENFORCEMENT MEASURES AND MONETARY LIABILITIES
                6.1 Penalties & Clearances: Willful non-compliance shall result in the immediate revocation of digital inter-operability tokens and immediate administrative inquiries.
                6.2 Fiscal Penalty Clause: Defaults exceeding 90 days incur a financial clawback penalty up to two percent (2%) of the agency's annual IT capital outlay.
                """.trimIndent(),
                summary = "2025 Revised Mandate superseding 2024 policy: Expands scope to CPSUs & Smart Cities, introduces Edge AI & Tier-4 multi-cloud, upgrades encryption to AES-256 GCM + TLS 1.3, lowers procurement review cap to INR 2.5 Crore, and tightens incident reporting to 6 hours.",
                sha256Hash = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
                isEncrypted = false,
                syncStatus = "SYNCED",
                chunkCount = 6
            ),
            DocumentEntity(
                id = 3,
                circularNumber = "DPIIT/PPP-MII/2024/09",
                title = "Public Procurement (Preference to Make in India) Order - High-Tech Electronics",
                department = "Ministry of Commerce & Industry / DPIIT",
                issueDate = "2024-06-18",
                category = "Procurement Directive",
                classification = "PUBLIC",
                fullText = """
                GOVERNMENT OF INDIA
                MINISTRY OF COMMERCE AND INDUSTRY
                DEPARTMENT FOR PROMOTION OF INDUSTRY AND INTERNAL TRADE
                ORDER No. DPIIT/PPP-MII/2024/09

                SUBJECT: MANDATORY LOCAL VALUE ADDITION IN PUBLIC PROCUREMENT OF ELECTRONICS AND COMPUTING APPLIANCES

                1. CLASSIFICATION OF BIDDERS
                1.1 Class-I Local Supplier: Means a supplier or service provider whose goods, services or works offered for procurement has local content equal to or more than 50%.
                1.2 Class-II Local Supplier: Local content equal to or more than 20% but less than 50%.
                1.3 Non-Local Supplier: Local content less than 20%.

                2. PURCHASE PREFERENCE RULES
                2.1 In public tenders up to INR 200 Crore, only Class-I and Class-II local suppliers shall be eligible to participate. Global tender enquiries are strictly barred without Cabinet Secretariat waiver.
                2.2 Margin of Purchase Preference: The margin of purchase preference shall be 20% for Class-I suppliers against lowest valid non-local offers.

                3. VERIFICATION AND PENALTIES
                3.1 False Declarations: False declaration of local value percentage constitutes breach of the Code of Integrity under Rule 175(1)(i)(h) of the General Financial Rules (GFR 2017) and invites debarment up to two (2) years.
                """.trimIndent(),
                summary = "Mandates 50% minimum local value addition for Class-I suppliers in public electronics tenders up to INR 200 Crore.",
                sha256Hash = "3b7b9319e7cfbd0e77d207797eb1df80c1097e33a6976865d1d643a60a76a5c1",
                isEncrypted = false,
                syncStatus = "SYNCED",
                chunkCount = 3
            ),
            DocumentEntity(
                id = 4,
                circularNumber = "08/DPDP/2025-CABSEC",
                title = "Inter-Departmental Citizen Data Protection & Verification Protocol",
                department = "Cabinet Secretariat / Ministry of Law and Justice",
                issueDate = "2025-01-05",
                category = "Statutory Guidelines",
                classification = "RESTRICTED",
                fullText = """
                CONFIDENTIAL - RESTRICTED CIRCULATION
                CABINET SECRETARIAT - GOVERNMENT OF INDIA
                CIRCULAR No. 08/DPDP/2025-CABSEC

                SUBJECT: SECURE PROTOCOLS FOR CITIZEN BENEFIT VERIFICATION AND PRIVACY COMPLIANCE

                1. MINIMUM DATA DISCLOSURE PRINCIPLE
                1.1 Departmental databases shall avoid exchanging raw demographic or biometric tables.
                1.2 Verification requests must be processed via boolean zero-knowledge tokens (e.g. Eligible: TRUE/FALSE) rather than transmitting full income or health logs.

                2. ENCRYPTION AND ACCESS LOG RETENTION
                2.1 All inter-agency queries must use end-to-end asymmetric key handshakes signed by departmental root certificates.
                2.2 Access logs with authenticated employee credentials must be preserved in immutable WORM (Write Once Read Many) digital ledgers for seven (7) years.
                """.trimIndent(),
                summary = "Restricted protocol governing zero-knowledge citizen data verification, purpose limitation, and 7-year tamper-evident log preservation.",
                sha256Hash = "7d441f27d441f27d441f27d441f27d441f27d441f27d441f27d441f27d441f27",
                isEncrypted = true,
                syncStatus = "SYNCED",
                chunkCount = 2
            )
        )
    }

    fun getInitialCollaborators(): List<CollaboratorEntity> {
        return listOf(
            CollaboratorEntity(
                id = "u-101",
                name = "Dr. Ananya Roy",
                role = "Director General (Policy Oversight)",
                avatarInitials = "AR",
                status = "ONLINE",
                activeDocTitle = "Cloud & AI Mandate 2025"
            ),
            CollaboratorEntity(
                id = "u-102",
                name = "Vikramaditya Sen",
                role = "Senior Legal Advisor (Cabinet Cell)",
                avatarInitials = "VS",
                status = "IN_ANALYSIS",
                activeDocTitle = "DPIIT Local Content Order"
            ),
            CollaboratorEntity(
                id = "u-103",
                name = "Pooja Deshmukh",
                role = "Chief Cybersecurity Auditor (CERT-In)",
                avatarInitials = "PD",
                status = "ONLINE",
                activeDocTitle = "Incident Reporting Norms"
            )
        )
    }

    fun getInitialNotes(): List<CollaborativeNoteEntity> {
        return listOf(
            CollaborativeNoteEntity(
                documentId = 2L,
                circularNumber = "MeitY/eGov/2025/12",
                authorName = "Pooja Deshmukh (CERT-In)",
                authorRole = "Chief Cybersecurity Auditor",
                noteText = "CRITICAL: Section 5.1 compresses breach notification to 6 hours. Directorate NOC workflows must be upgraded prior to Q3 audit.",
                clauseTag = "Section 5.1 (Incident Window)",
                timestamp = System.currentTimeMillis() - (25 * 60 * 1000)
            ),
            CollaborativeNoteEntity(
                documentId = 2L,
                circularNumber = "MeitY/eGov/2025/12",
                authorName = "Dr. Ananya Roy",
                authorRole = "Director General (Policy)",
                noteText = "Verified compliance with MeitY Edge AI guidelines. Model quantization INT4 allows sovereign edge inference without data egress.",
                clauseTag = "Section 6.1 (Edge AI & Gemma)",
                timestamp = System.currentTimeMillis() - (55 * 60 * 1000)
            ),
            CollaborativeNoteEntity(
                documentId = 3L,
                circularNumber = "DPIIT/PPP-MII/2024/09",
                authorName = "Vikramaditya Sen",
                authorRole = "Senior Legal Advisor",
                noteText = "Procurement sub-committee confirmed Class-I supplier preference clause applies to local server hardware & sovereign AI appliances.",
                clauseTag = "Section 2.1 (Purchase Preference)",
                timestamp = System.currentTimeMillis() - (120 * 60 * 1000)
            )
        )
    }
}
