## 1.5.0
* Initial module metadata (FOLIO-2003)
* Fix security vulnerabilities
* Add links to README additional info (FOLIO-473)

## 1.4.0
* Investigate mod-calendar module freeze (MODCAL-40)
* Error setting exceptional closing hours for single day (MODCAL-38)
* Mod-calendar refactoring (MODCAL-41)

## 1.3.0
* Fix security vulnerabilities reported in jackson-databind (MODCAL-28)
* Change calculate opening endpoint (MODCAL-33 MODCAL-34 MODCAL-29)
* Update copyright year (FOLIO-1021)
* Use the same Vertx instance for all postgres clients (MODCAL-33 MODCAL-34)
* Save exceptional period error fix (MODCAL-35)
* The created period or exception period is not deleted in mod-calendar (MODCAL-36)
* Change GET and POST endpoints to avoid timezone issues (MODCAL-31)
* Bug API GET: /periods/{servicePointId}/period related to getting more than one period (MODCAL-37)

## 1.2.0
* Add endpoint /calendar/periods/{servicePointId}/calculateopening with GET to return open periods for given Service Point
## 1.0.3
* MODCAL-19 - Fix security vulnerability reported in jackson-databind
* MODCAL-20 - Fix missing description fields in RAML JSON schemas
## 1.0.2
* MODCAL-1 Added exceptional opening day management
* MODCAL-5 Added filtering for events by date
* MODCAL-6 Use event types instead of constants
* MODCAL-7 Design database schema
* MODCAL-8 Modify backend based on new API design
* MODCAL-9 Regular endpoint
* MODCAL-10 Modify API
* MODCAL-11 Exceptional endpoint
* MODCAL-12 Opening endpoints
* MODCAL-13 includeClosedDays queryparam implemented
* MODCAL-14 release 1.0.0
* MODCAL-15 Vagrant box bug - vertx version increased
* MODCAL-13 actualOpening queryparam implemented
