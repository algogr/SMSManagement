package gr.algo.SMSManagement

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name="incoming")

data class IncomingSMS(@Id @GeneratedValue
                       val id: Int=-1,@Column val fromsender:String,@Column val dateReceived: LocalDateTime,@Column val message:String,@Column val PDUMessage:String )
