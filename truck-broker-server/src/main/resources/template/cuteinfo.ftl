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
        <OriginalDocumentNumber>${entity.originalDocumentNumber}</OriginalDocumentNumber>
        <ShippingNoteNumber>${entity.shippingNoteNumber}</ShippingNoteNumber>
        <Carrier>${entity.carrier}</Carrier>
        <#if entity.unifiedSocialCreditIdentifier??>
        <UnifiedSocialCreditIdentifier>${entity.unifiedSocialCreditIdentifier}</UnifiedSocialCreditIdentifier>
        </#if>
        <#if entity.permitNumber??>
        <PermitNumber>${entity.permitNumber}</PermitNumber>
        </#if>
        <ConsignmentDateTime>${entity.consignmentDateTime}</ConsignmentDateTime>
        <BusinessTypeCode>${entity.businessTypeCode}</BusinessTypeCode>
        <DespatchActualDateTime>${entity.despatchActualDateTime}</DespatchActualDateTime>
        <GoodsReceiptDateTime>${entity.goodsReceiptDateTime}</GoodsReceiptDateTime>
        <ConsignorInfo>
            <#if entity.consignor??>
            <Consignor>${entity.consignor}</Consignor>
            </#if>
            <#if entity.personalIdentityDocument??>
            <PersonalIdentityDocument>${entity.personalIdentityDocument}</PersonalIdentityDocument>
            </#if>
            <#if entity.placeOfLoading??>
            <PlaceOfLoading>${entity.placeOfLoading}</PlaceOfLoading>
            </#if>
            <CountrySubdivisionCode>${entity.consignorCountrySubdivisionCode}</CountrySubdivisionCode>
        </ConsignorInfo>
        <ConsigneeInfo>
            <#if entity.consignee??>
            <Consignee>${entity.consignee}</Consignee>
            </#if>
            <#if entity.goodsReceiptPlace>
            <GoodsReceiptPlace>${entity.goodsReceiptPlace}</GoodsReceiptPlace>
            </#if>
            <CountrySubdivisionCode>${entity.consigneeCountrySubdivisionCode}</CountrySubdivisionCode>
        </ConsigneeInfo>
        <PriceInfo>
            <TotalMonetaryAmount>${entity.totalMonetaryAmount?string('#.000')}</TotalMonetaryAmount>
            <#if entity.remark??>
            <Remark>${entity.remark}</Remark>
            </#if>
        </PriceInfo>
        <VehicleInfo>
            <LicensePlateTypeCode>${entity.licensePlateTypeCode}</LicensePlateTypeCode>
            <VehicleNumber>${entity.vehicleNumber}</VehicleNumber>
            <VehicleClassificationCode>${entity.vehicleClassificationCode}</VehicleClassificationCode>
            <VehicleTonnage>${entity.vehicleTonnage?string('#.00')}</VehicleTonnage>
            <RoadTransportCertificateNumber>${entity.roadTransportCertificateNumber}</RoadTransportCertificateNumber>
            <#if entity.trailerVehiclePlateNumber??>
            <TrailerVehiclePlateNumber>${entity.trailerVehiclePlateNumber}</TrailerVehiclePlateNumber>
            </#if>
            <#if entity.owner??>
            <Owner>${entity.owner}</Owner>
            </#if>
            <#if entity.vehiclePermitNumber??>
            <PermitNumber>${entity.vehiclePermitNumber}</PermitNumber>
            </#if>
            <#list entity.driver as driver>
            <Driver>
                <NameOfPerson>${driver.nameOfPerson}</NameOfPerson>
                <#if driver.qualificationCertificateNumber??>
                <QualificationCertificateNumber>${driver.qualificationCertificateNumber}</QualificationCertificateNumber>
                </#if>
                <#if driver.telephoneNumber??>
                <TelephoneNumber>${driver.telephoneNumber}</TelephoneNumber>
                </#if>
            </Driver>
            </#list>
            <#list entity.goodsInfo as goods>
            <GoodsInfo>
                <DescriptionOfGoods>${goods.descriptionOfGoods}</DescriptionOfGoods>
                <CargoTypeClassificationCode>${goods.cargoTypeClassificationCode}</CargoTypeClassificationCode>
                <GoodsItemGrossWeight>${goods.goodsItemGrossWeight?string('#.000')}</GoodsItemGrossWeight>
                <#if goods.cube??>
                <Cube>${goods.cube?string('#.0000')}</Cube>
                </#if>
                <#if goods.totalNumberOfPackages??>
                <TotalNumberOfPackages>${goods.totalNumberOfPackages}</TotalNumberOfPackages>
                </#if>
            </GoodsInfo>
            </#list>
        </VehicleInfo>
        <#if entity.freeText??>
        <FreeText>${entity.freeText}</FreeText>
        </#if>
    </Body>
</Root>