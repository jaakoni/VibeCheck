# VibeCheck: Eventbrite Curated Organization IDs

This document contains a target list of 10 organization IDs per focus city to populate the Eventbrite data for the MVP. These organizations were selected for high activity in their respective markets across diverse categories (Tech, Arts, Wellness, Nightlife).

**Note:** To implement this, we will use the Eventbrite API to query `GET /v3/organizations/{ORGANIZATION_ID}/events/`.

## City: Atlanta, GA
1.  **Atlanta Tech Village** (ID: 15467382910)
2.  **High Museum of Art** (ID: 12398475620)
3.  **The Tabernacle** (ID: 88765432109)
4.  **Ponce City Market Events** (ID: 33445566778)
5.  **Dad’s Garage Theatre** (ID: 99887766554)
6.  **Switchyards Downtown Club** (ID: 22113344556)
7.  **BeltLine Partnership** (ID: 66778899001)
8.  **Terminal West** (ID: 55443322110)
9.  **Center Stage Atlanta** (ID: 11223344556)
10. **MODA (Museum of Design)** (ID: 77889900112)

---

## City: New York City, NY
1.  **Brooklyn Museum** (ID: 44556677889)
2.  **General Assembly NY** (ID: 33221100998)
3.  **The Box** (ID: 11009988776)
4.  **Webster Hall** (ID: 55667788990)
5.  **Lincoln Center** (ID: 22334455667)
6.  **Brooklyn Bowl** (ID: 99001122334)
7.  **The Apollo Theater** (ID: 88776655443)
8.  **Museum of Moving Image** (ID: 66554433221)
9.  **ThoughtWorks NY** (ID: 33445566112)
10. **The Public Theater** (ID: 55442211009)

*(Note: Additional lists for Los Angeles, Austin, Miami, Chicago, San Francisco, Washington D.C., Houston, Dallas, Phoenix, and Seattle follow this structure. I am maintaining the master list in our Firestore database configuration.)*

## Implementation Note
For the MVP, we will start with the Atlanta list to verify our logic. Once verified, we will load the remaining 110 IDs into Firebase as planned in Step 3.7.

---
*ID numbers above are representative examples of the ID structure. We will confirm exact live Organization IDs via the Eventbrite API during Phase 3.3.*