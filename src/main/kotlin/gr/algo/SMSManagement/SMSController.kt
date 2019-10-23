package gr.algo.SMSManagement

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
//import org.apache.poi.ss.usermodel.Cell
//import org.apache.poi.ss.usermodel.CellType
//import org.apache.poi.ss.usermodel.Row
//import org.apache.poi.ss.usermodel.Sheet
//import org.apache.poi.ss.usermodel.Workbook
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Controller
class SMSController{

    @Autowired
    lateinit var incomingSMSRepository: IncomingSMSRepository

    @GetMapping("/")
    fun index(model: Model):String{
        model.addAttribute("today","Phone")
        return "index"
    }
    @GetMapping("/sendsms")
    fun send(@RequestParam phonesList:String,@RequestParam message:String,@RequestParam line:String,model:Model):String{
        val limit=70
        val span=line
        val mLength=message.length
        val times:Int = mLength.div(limit)
        val rem:Int=mLength.rem(limit)
        val phones:List<String> = phonesList.split(",")
        var smsmsg:String

        if (times==0){
            for  (phone in phones)
            {
                println("Ms:$message$phone")
                val strArray:Array<String> =  arrayOf("asterisk","-rx","gsm send sms $span $phone \"$message\"")
                println("Start")
                println(strArray[0])
                println(strArray[1])
                println(strArray[2])
                val p=Runtime.getRuntime().exec(strArray)


                // val sc = Scanner(p.inputStream)

            }



        }
        else {
            for (phone in phones) {
                for (i: Int in 0..times) {
                    println("i:$i")
                    println("Times:$times")
                    println("Mhkow:${message.length}")
                    var endchar:Int=(i*70)+70
                    if ((i * 70) + 70>message.length-1) {

                        endchar = message.length - 1
                        kotlin.io.println("Looparisa")
                    }

                    println("Endchar:$endchar")
                    val part: String = message.substring(i * 70, endchar)
                    println("Ms:$message$phone")
                    val strArray: Array<String> = arrayOf("asterisk", "-rx", "gsm send sms $span $phone \"$part\"")
                    println("Start")
                    println(strArray[0])
                    println(strArray[1])
                    println(strArray[2])
                    val p = Runtime.getRuntime().exec(strArray)


                }

            }
        }
       return "index"
    }

    @GetMapping("/fetchno")

    fun fetchno(model:Model):String{
        /*
        val excelFile = FileInputStream(File("/home/jim/Downloads/tmp/Desktop Folder/telno.xlsx"))
        val workBook = XSSFWorkbook(excelFile)
        val sheet = workBook.getSheet("Sheet1")
        val rows = sheet.iterator()
        var phoneList = mutableListOf<String>()
        while (rows.hasNext())
        {
            val currrentRow= rows.next()
            val cellsInRow = currrentRow.iterator()
            val currentCell= cellsInRow.next()
            val phoneNumber = currentCell.numericCellValue.toString()
            var fphoneNumber:String=String.format("%.0f",phoneNumber)
            phoneList.add(fphoneNumber)

        }
        workBook.close()
        excelFile.close()
        println(phoneList)
        var strPhoneList = phoneList.joinToString()
        model.addAttribute("today", strPhoneList)
        */
        return "index"
    }

    @GetMapping("incoming")
    fun getIncomingSMS(model: Model):String{
        val lastDateTime: LocalDateTime= LocalDateTime.now()
        val lastSMS:IncomingSMS=incomingSMSRepository.findFirstByOrderByDateReceivedDesc()
        val fileName = "/home/jim/Downloads/tmp/Desktop Folder/receive_sms"
        val lines: List<String> = File(fileName).readLines()


        var recDate: LocalDateTime = LocalDateTime.now()
        var sender: String= ""
        var textContent:String=""
        var PDUContent:String=""
        var SMS: IncomingSMS
        lines.forEach {
                if (it.contains("START"))
                {
                    return@forEach
                }

                if (it.contains("Time")){
                    val datestr=it.substring(it.indexOf(": ")+2)
                    recDate=LocalDateTime.parse(datestr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    return@forEach
                }

                if (it.contains("Sender")){
                    sender = it.substring(it.indexOf(": ")+2)
                    return@forEach
                }

                if (it.contains("Text")){
                    textContent = it.substring(it.indexOf(": ")+2)
                    return@forEach
                }

                if (it.contains("PDU")){
                    println("AYTO EIMAI: $it")
                    PDUContent = Converter .toText(it.substring(it.indexOf(": ")+6))
                    return@forEach
                }



                if (it.contains("END")) {
                    val SMS=IncomingSMS(fromsender=sender,dateReceived=recDate,message=textContent,PDUMessage=PDUContent)
                    if (SMS.dateReceived > lastSMS.dateReceived)
                    {
                        println("SENDER:$SMS")
                        incomingSMSRepository.save(SMS)
                    }
                    return@forEach

                }

                else {

                    return@forEach
                }


            }
        //model.addAttribute("smslist",SMSList)
        return "incoming"
    }


    @GetMapping("/querysms")
    fun querySMS(@RequestParam fromDate:String,@RequestParam toDate:String,model: Model):String
    {

        val parFromDate=LocalDateTime.parse(fromDate+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val parToDate=LocalDateTime.parse(toDate+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val parFromDate=LocalDateTime.parse("2016-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val SMSList=incomingSMSRepository.findAllByDateReceivedBetweenOrderByDateReceivedDesc(parFromDate,parToDate)


        val converter = Converter.toText("0791039624910000040ED0D637396C7EBBCB0000711032815193809996E0B2487D829A4FE713D42C8268C510885A810641CDA7F3099A2483204CD11974B95CD427685A951A412650339864829A45103508B2C1604D2168920C82624DA433C802359FCE27A85904D18AA0A0939A044D924190AD18024D92415070F2840641D4671664092E8BD4671684A116939422A8560361A84F506C56AB818A150C48867BC16221")
        // val pdu = PDU("0791039624910000040ED0D637396C7EBBCB0000711032815193809996E0B2487D829A4FE713D42C8268C510885A810641CDA7F3099A2483204CD11974B95CD427685A951A412650339864829A45103508B2C1604D2168920C82624DA433C802359FCE27A85904D18AA0A0939A044D924190AD18024D92415070F2840641D4671664092E8BD4671684A116939422A8560361A84F506C56AB818A150C48867BC16221")
        // val str = pdu.userData



        //println("TESTATARA $converter")
        model.addAttribute("smslist",SMSList)
        return  "incoming"

    }
}