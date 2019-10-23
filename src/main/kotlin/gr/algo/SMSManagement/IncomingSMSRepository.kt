package gr.algo.SMSManagement

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
@Repository
interface IncomingSMSRepository: JpaRepository<IncomingSMS, Int> {
    //fun findAllBydateReceived(dt:LocalDateTime) :List<IncomingSMS>
    fun findFirstByOrderByDateReceivedDesc() :IncomingSMS
    fun findAllByDateReceivedBetweenOrderByDateReceivedDesc(fromDate:LocalDateTime,toDate:LocalDateTime): List<IncomingSMS>
}