def issueKey = 'IA-1'

def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    logger.info( 'result.body.fields')
    //return result.body.fields

} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

def pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
def myDateString = "2020-02-01T22:34:00.000-0500"
def myDate = Date.parse(pattern, myDateString)

def currentDate = new Date()
def currentTimeInMS = currentDate.getTime()
def fieldTimeInMS = myDate.getTime()
logger.info('currentTimeInMS->'+currentTimeInMS)

long differenceInMinutes = (currentTimeInMS - fieldTimeInMS)/1000/60
logger.info('The difference in minutes is: ' + differenceInMinutes)

def base = new Date(0)
def seconds = differenceInMinutes // 15 days, 3 hours and 5 minutes
def duration = new Date(seconds * 1000)
use(groovy.time.TimeCategory) {
    println duration-base
}
return differenceInMinutes
