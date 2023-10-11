def issueKey = 'HST-10218' // issue.key

def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    logger.info('Ok sigue')
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
//return result.body.fields.assignee
def asignado = result.body.fields.assignee.displayName
def asignadoId = result.body.fields.assignee.accountId
def update
def area = "customfield_10433"
def teamOwner =  "customfield_10434"
// Declarar y asignar valores a un array para los usuarios 
/*
5f0c76a41084620015a533f8 // Eduardo Ortiz
62221dd34160640069c7b622 // Angela López Egea
712020:cbfd1eb9-55af-460f-94e5-2ec9240d4a20 // Anna Hernandez
62752def807e0000691e3670 // Felix Gomez
63e10f98c3eb74ad8e98a193 // Exo
61517c9f99b4b8006a3612d2 // Functionalscripts
62cd58ffbd54f8d3ffb62eeb // Data-analytics
70121:71d3c2d6-1128-4ae9-bdbe-a30bdfaccf44 // Marta Rubió
70121:e2e63136-fe9b-409c-9f5d-28c04d413b0d // Adrià Lopez
5f33b7bad0884f004967abd1 //Coord SEO
5e26c11ed5bb640e71368d96 // Alber Tarrades
5dd2621a57e9480e500a3a75 // LLuis Benet
557058:68a8dd9a-4739-4847-921b-db43a655b465 // Ivan Leon
5e677c1e84dcfc0cf3912278 // Maria Gomez
5db05f24dea2ad0c348474dd // Jordi trapero
6092552044245700711c8dcb // Jose Luis Cayuela
557058:9b23dcfd-aed6-4d75-9bda-95510b88f1ec // Sergi GOmez
712020:a5bb0d3d-fa14-48e0-aa52-284083ce08d0 // Mario santos
712020:fa1c874a-f53a-489e-9b61-886400add6f8 // seat.code.dealers
712020:1cdc3873-a973-4557-84f7-fa5a3c020a0a // Alba losada
712020:5830e142-2b87-4bc3-b2a3-5f7ffdeae26d // seat.code.discovery
712020:07434b81-6a9d-44c2-b29f-f89dd66159fc // seat.code.cc
712020:5201462b-ab23-4069-8ae1-f03d352fdca0 // Javier ruiz
712020:ae333d54-f370-4653-83c1-7cb75ce2a01b // Seat.code.ovs
5ffc27b844065f013fdf23cb // Islando lopez
712020:b7c03a76-e1b2-45f3-8c54-12ce8868d4ec // Adriana jover
62d556149189e98a2017dd5a //Private area suad
600566beea0e64006b500858 // Carlos gonzalez
5f5a0a04bea5be006825849b // Rainer F
712020:082c6600-f2f0-4e9d-8323-7d218f634063 // Ignasi B
712020:87cd5b8c-7a65-43e9-8273-8d23a6ce9345 // seat.code.stocklocator 
712020:de89743b-d39b-4f83-abe5-04e539f32fcf // Sergio L
64268b186b29c052ab2fa643 // Flor
642690345534b0bf744337ff // Lucila
63fdbd8c15d668edd8ed2986 // Miki
5d3aa0bb70e3c90c952fb19d //Jose I balaguer
70121:84ddbef1-6807-49ea-8222-b8333c4233b0 // Xavier treserra
5f02d335b545e200154b1e7a // Jose MOreno
6177de0c327da400691c0c58// Javier lopez
631617073778a7aadf197811 // Luis F Alejano
712020:164d762e-94c4-4cb3-9ae5-2c21ad667fd5 // Natalie R
642693dbb05b4e3e7dac1cc8 // Adriana Melero
6177da7058006300696ef216 // JM Vehi
712020:7f209b9d-68b8-4e08-944d-f56e509ca64f // Miriam Fleta
712020:77548515-8101-47e4-986b-0938accd189c // Ariadna Soto
712020:5ca420e3-377a-4a17-b83d-c17100e9f05e // Nydia 
5ebac86d06a3eb0b7e45cb34 // Nydia 
70121:c83955bc-4127-4a4a-a280-5f7ded599269 // David Lozano
5f74922058899e00705bc0b4 // Jordi Aragones
712020:426aaae2-5930-4fa4-a82b-f9cd89eb082b // Ruben Betes
5ede48284833d00aae234420 // International Marketing
*/
def Platforms = "5f0c76a41084620015a533f8"
def Analitica = ["62221dd34160640069c7b622", "712020:cbfd1eb9-55af-460f-94e5-2ec9240d4a20","62752def807e0000691e3670",
"63e10f98c3eb74ad8e98a193", "61517c9f99b4b8006a3612d2", "62cd58ffbd54f8d3ffb62eeb"]
def SEO = ["70121:71d3c2d6-1128-4ae9-bdbe-a30bdfaccf44", "70121:e2e63136-fe9b-409c-9f5d-28c04d413b0d", "5f33b7bad0884f004967abd1"]
def CodeNoSquad = ["5e26c11ed5bb640e71368d96" , "5dd2621a57e9480e500a3a75"]
def HQ = [ "557058:68a8dd9a-4739-4847-921b-db43a655b465", "5e677c1e84dcfc0cf3912278", "5db05f24dea2ad0c348474dd", "6092552044245700711c8dcb",
"557058:9b23dcfd-aed6-4d75-9bda-95510b88f1ec" ]
def CODEDealers = ["712020:a5bb0d3d-fa14-48e0-aa52-284083ce08d0", "712020:fa1c874a-f53a-489e-9b61-886400add6f8"]
def CODEDiscovery = [ "712020:1cdc3873-a973-4557-84f7-fa5a3c020a0a", "712020:5830e142-2b87-4bc3-b2a3-5f7ffdeae26d"]
def CODECC = ["712020:07434b81-6a9d-44c2-b29f-f89dd66159fc", "712020:5201462b-ab23-4069-8ae1-f03d352fdca0"]
def OVS = ["712020:ae333d54-f370-4653-83c1-7cb75ce2a01b", "5ffc27b844065f013fdf23cb", "712020:b7c03a76-e1b2-45f3-8c54-12ce8868d4ec"]
def CODESquad =["62d556149189e98a2017dd5a ","600566beea0e64006b500858"]
def ConLeos = ["5f5a0a04bea5be006825849b"]
def StockLocator = ["712020:082c6600-f2f0-4e9d-8323-7d218f634063", "712020:87cd5b8c-7a65-43e9-8273-8d23a6ce9345"]
def Hs = [ "712020:de89743b-d39b-4f83-abe5-04e539f32fcf","64268b186b29c052ab2fa643", "642690345534b0bf744337ff", "63fdbd8c15d668edd8ed2986" ]
def RetailTools = ["5d3aa0bb70e3c90c952fb19d", "70121:84ddbef1-6807-49ea-8222-b8333c4233b0"]
def OneShop = ["5f02d335b545e200154b1e7a", "6177de0c327da400691c0c58"]
def Market = ["712020:164d762e-94c4-4cb3-9ae5-2c21ad667fd5", "712020:7f209b9d-68b8-4e08-944d-f56e509ca64f", "712020:77548515-8101-47e4-986b-0938accd189c ", 
"712020:5ca420e3-377a-4a17-b83d-c17100e9f05e", "631617073778a7aadf197811", "642693dbb05b4e3e7dac1cc8", "6177da7058006300696ef216", "5ebac86d06a3eb0b7e45cb34"]
def SWN = ["70121:c83955bc-4127-4a4a-a280-5f7ded599269", "5f74922058899e00705bc0b4", "712020:426aaae2-5930-4fa4-a82b-f9cd89eb082b"]
def IntMK = ["5ede48284833d00aae234420"]

if (asignadoId == Platforms){
logger.info('Entra en Platform')
 update = put('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .body([
        fields:[
                customfield_10433: [     // area
                    id: "11964"
                ],
                customfield_10434: [     // Team Owner
                    id: "11971"
                ] 
        ]
])
        .asString()
if (result.status == 204) {
    return 'Success'
} else {
    return "${result.status}: ${result.body}"
}
    logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para Platforms')
}

for (def x=0;x<Analitica.size();x++){
    if (Analitica[x] == asignadoId ){
	logger.info('Entra en Analitica')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11960"
						],
						 customfield_10434: [     // Team Owner
							id: "11965"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para Analitica')
		}
	}


for (def x=0;x<SEO.size();x++){
    if (SEO[x] == asignadoId ){
		logger.info('Entra en SEO')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11960"
						],
						 customfield_10434: [     // Team Owner
							id: "12004"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para SEO')
		}
	}

for (def x=0;x<CodeNoSquad.size();x++){
    if (CodeNoSquad[x] == asignadoId ){
		logger.info('Entra en CodeNoSquad')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12006"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<HQ.size();x++){
    if (HQ[x] == asignadoId ){
			logger.info('Entra en HQ')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12005"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}


for (def x=0;x<CODEDealers.size();x++){
    if (CODEDealers[x] == asignadoId ){
			logger.info('Entra en CodeDealers')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "11999"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}


for (def x=0;x<CODEDiscovery.size();x++){
    if (CODEDiscovery[x] == asignadoId ){
			logger.info('Entra en CodeDiscovery')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "11998"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}


for (def x=0;x<CODECC.size();x++){
    if (CODECC[x] == asignadoId ){
			logger.info('Entra en CodeCC')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12000"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}


for (def x=0;x<OVS.size();x++){
    if (OVS[x] == asignadoId ){
			logger.info('Entra en OVS')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12003"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}


for (def x=0;x<CODESquad.size();x++){
    if (CODESquad[x] == asignadoId ){
			logger.info('Entra en CodeSquad')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12001"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}


for (def x=0;x<ConLeos.size();x++){
    if (ConLeos[x] == asignadoId ){
			logger.info('Entra en Conleos')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "11972"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<StockLocator.size();x++){
    if (StockLocator[x] == asignadoId ){
			logger.info('Entra en SL')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12002"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	
}

for (def x=0;x<Hs.size();x++){
    if (Hs[x] == asignadoId ){
			logger.info('Entra en HS')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "11970"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<RetailTools.size();x++){
    if (RetailTools[x] == asignadoId ){
			logger.info('Entra en RetailTools')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11962"
						],
						 customfield_10434: [     // Team Owner
							id: "11976"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<OneShop.size();x++){
    if (OneShop[x] == asignadoId ){
				logger.info('Entra en OneShop')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "12676"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<Market.size();x++){
    if (Market[x] == asignadoId ){
				logger.info('Entra en Market')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "11969"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<SWN.size();x++){
    if (SWN[x] == asignadoId ){
				logger.info('Entra en SWN')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11964"
						],
						 customfield_10434: [     // Team Owner
							id: "11977"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

for (def x=0;x<IntMK.size();x++){
    if (IntMK[x] == asignadoId ){
				logger.info('Entra en IntMK')

		 update = put('/rest/api/2/issue/' + issueKey)
				.header('Content-Type', 'application/json')
				.body([
				fields:[
						customfield_10433: [     // area
							id: "11963"
						],
						 customfield_10434: [     // Team Owner
							id: "11973"
						] 
				]
		])
				.asString()
		if (result.status == 204) {
			return 'Success'
		} else {
			return "${result.status}: ${result.body}"
		}
		logger.info('assignee = '+asignado+', assigneeId = '+asignadoId+', para CodeNoSquad')
		}
	}

