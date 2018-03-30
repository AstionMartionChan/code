<?xml version="1.0" encoding="UTF-8"?>
<Root>
    <Header>
        <MessageReferenceNumber>${entity.messageReferenceNumber}</MessageReferenceNumber>
        <DocumentName>${entity.documentName}</DocumentName>
        <DocumentVersionNumber>${entity.documentVersionNumber}</DocumentVersionNumber>
        <SenderCode>${entity.senderCode}</SenderCode>
        <RecipientCode>${entity.recipientCode}</RecipientCode>
        <MessageSendingDateTime>${entity.messageSendingDateTime}</MessageSendingDateTime>
        <#if entity.messageFunctionCode??>
        <MessageFunctionCode>${entity.messageFunctionCode}</MessageFunctionCode>
        </#if>
    </Header>
    <Body>
        <DocumentNumber>${entity.documentNumber}</DocumentNumber>
        <Carrier>${entity.carrier}</Carrier>
        <VehicleNumber>${entity.vehicleNumber}</VehicleNumber>
        <LicensePlateTypeCode>${entity.licensePlateTypeCode}</LicensePlateTypeCode>
        <#list entity.shippingNoteList as shippingNote>
        <ShippingNoteList>
            <ShippingNoteNumber>${shippingNote.shippingNoteNumber}</ShippingNoteNumber>
            <Remark>${shippingNote.remark}</Remark>
        </ShippingNoteList>
        </#list>
        <#list entity.financialList as financial>
        <Financiallist>
            <PaymentMeansCode>${financial.paymentMeansCode}</PaymentMeansCode>
            <#if financial.bankCode>
            <BankCode>${financial.bankCode}</BankCode>
            </#if>
            <SequenceCode>${financial.sequenceCode}</SequenceCode>
            <MonetaryAmount>${financial.monetaryAmount}</MonetaryAmount>
            <DateTime>${financial.dateTime}</DateTime>
        </Financiallist>
        </#list>
    </Body>
</Root>