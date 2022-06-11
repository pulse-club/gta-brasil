SCRIPT_START
{
    LVAR_INT hVeh pExternalLabel //In
    LVAR_INT scplayer nModel slot type pLabel itemPrice i j k opacity curModel flags totalCost paintjobCost colorCost installedPaintjob wheelShopSelected
    LVAR_FLOAT x y z sx sy slotsPosX slotsPosY slotsSizeX slotsSizeY majorSlotWidth tempx
    LVAR_TEXT_LABEL itemName

    // Wheel sets
    CONST_INT ANY_SET           -1
    CONST_INT WHEEL_SET_TF      0   // Transfender
    CONST_INT WHEEL_SET_WAA     1   // Wheel Arch Angel
    CONST_INT WHEEL_SET_LLC     2   // Loco Low Co.
    CONST_INT WHEEL_SET_MORE    3   // More...

    // Upgrade types
    CONST_INT UPGRADE_HOOD    				0
    CONST_INT UPGRADE_VENT    				1
    CONST_INT UPGRADE_SPOIL   				2
    CONST_INT UPGRADE_SIDESKIRT				3
    CONST_INT UPGRADE_BULLFRNT				4
    CONST_INT UPGRADE_BULLREAR				5
    CONST_INT UPGRADE_LIGHTS  				6
    CONST_INT UPGRADE_ROOF    				7
    CONST_INT UPGRADE_NITRO   				8
    CONST_INT UPGRADE_HYDRAULICS			9
    CONST_INT UPGRADE_STEREO  				10
    CONST_INT UPGRADE_UNKNOWN  				11
    CONST_INT UPGRADE_WHEELS  				12
    CONST_INT UPGRADE_EXHAUST 				13
    CONST_INT UPGRADE_BUMPFRNT				14
    CONST_INT UPGRADE_BUMPREAR				15
    CONST_INT UPGRADE_MISC					16
    CONST_INT UPGRADE_ALLWHEELS             17

    // Sprites
    
    CONST_INT SPRITE_TUNING0    1
    CONST_INT SPRITE_TUNING1    2
    CONST_INT SPRITE_TUNING2    3
    CONST_INT SPRITE_TUNING3    4
    CONST_INT SPRITE_TUNING4    5
    CONST_INT SPRITE_TUNING5    6
    CONST_INT SPRITE_TUNING6    7
    CONST_INT SPRITE_TUNING7    8
    CONST_INT SPRITE_TUNING8    9
    CONST_INT SPRITE_TUNING9    10
    CONST_INT SPRITE_TUNING10   11
    //CONST_INT SPRITE_TUNING11 12
    CONST_INT SPRITE_TUNING12   13
    CONST_INT SPRITE_TUNING13   14 
    CONST_INT SPRITE_TUNING14   15
    CONST_INT SPRITE_TUNING15   16
    CONST_INT SPRITE_TUNINGC    17 
    CONST_INT SPRITE_TUNINGP    18
    CONST_INT SPRITE_TUNINGO    19
    CONST_INT SPRITE_BLANK      20 
    CONST_INT SPRITE_SHOPCART   21
    CONST_INT SPRITE_CAR        22
    CONST_INT SPRITE_DEFAULT    23

    // Flags
    CONST_INT DISABLE_CMD_ARROW 1
    CONST_INT DISABLE_CMD 2

    // Addresses
    CONST_INT CModelInfo__GetModelInfo 0x00403DA0

///////////////////////////////////////////////////////////////////////////////////////////////////

    /*IF pExternalLabel = 0 // auto started by .cs
        WHILE NOT TEST_CHEAT UG
            WAIT 0
        ENDWHILE
        IF IS_CHAR_SITTING_IN_ANY_CAR scplayer
            STORE_CAR_CHAR_IS_IN_NO_SAVE scplayer hVeh
        ELSE
            REQUEST_MODEL ELEGY
            LOAD_ALL_MODELS_NOW
            GET_OFFSET_FROM_CHAR_IN_WORLD_COORDS scplayer 0.0 3.0 0.0 x y z
            CREATE_CAR ELEGY x y z (hVeh)
            WARP_CHAR_INTO_CAR scplayer hVeh
        ENDIF
        GET_LABEL_POINTER Test pExternalLabel
    ENDIF*/
    IF pExternalLabel = 0
        TERMINATE_THIS_CUSTOM_SCRIPT
    ENDIF

    GET_PLAYER_CHAR 0 (scplayer)

    flags = 0

    GOSUB LoadFiles

    GET_CAR_MODEL hVeh (nModel)

    CLEO_CALL StoreAllUpgradesToCar 0 (nModel)()
    CLEO_CALL StoreAllWheels 0 ()()
    CLEO_CALL StoreAllInstalledParts 0 (hVeh)()
    CLEO_CALL StoreAllInstalledColors 0 (hVeh)()
    GET_CURRENT_VEHICLE_PAINTJOB hVeh installedPaintjob

    LOAD_PRICES CARMODS
    
    totalCost = 0
    slot = 0
    type = -2
    wheelShopSelected = -1

    WHILE TRUE

        IF type > -1 // not color or paintjob
            IF IS_KEY_PRESSED VK_UP
            AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD_ARROW
                SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD_ARROW
                slot--
                IF slot < 0
                    CLEO_CALL GetLabelFromType 0 (type wheelShopSelected)(pLabel)
                    CLEO_CALL CountSlots 0 (pLabel type wheelShopSelected)(slot)
                    slot--
                ENDIF
            ENDIF

            IF IS_KEY_PRESSED VK_DOWN
            AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD_ARROW
                SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD_ARROW
                slot++
                CLEO_CALL GetLabelFromType 0 (type wheelShopSelected)(pLabel)
                CLEO_CALL CountSlots 0 (pLabel type wheelShopSelected)(j)
                j--
                IF slot > j
                    slot = 0
                ENDIF
            ENDIF
        ENDIF

        IF IS_KEY_PRESSED VK_RIGHT
        AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD_ARROW
            SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD_ARROW
            wheelShopSelected = -1
            slot = 0 
            WHILE TRUE
                type++
                GET_CAR_MODEL hVeh j
                IF IS_THIS_MODEL_A_BOAT J
                    j = 16
                ELSE
                    j = 17
                ENDIF
                IF type > j
                    type = -2
                    BREAK
                ENDIF
                IF type < 0 // color or paintjob
                    IF type = -1
                        GET_NUM_AVAILABLE_PAINTJOBS hVeh i
                        IF NOT i > 0
                            CONTINUE
                        ENDIF
                    ENDIF
                    BREAK 
                ENDIF
                CLEO_CALL GetLabelFromType 0 (type wheelShopSelected)(pLabel)
                IF CLEO_CALL LabelNotEmpty 0 (pLabel)
                    BREAK
                ENDIF
            ENDWHILE
        ENDIF

        IF IS_KEY_PRESSED VK_LEFT
        AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD_ARROW
            SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD_ARROW
            wheelShopSelected = -1
            slot = 0
            WHILE TRUE
                type--
                GET_CAR_MODEL hVeh j
                IF IS_THIS_MODEL_A_BOAT J
                    j = 16
                ELSE
                    j = 17
                ENDIF
                IF type < -2
                    type = j
                ENDIF
                IF type < 0 // color or paintjob
                    IF type = -1
                        GET_NUM_AVAILABLE_PAINTJOBS hVeh i
                        IF NOT i > 0
                            CONTINUE
                        ENDIF
                    ENDIF
                    BREAK 
                ENDIF
                CLEO_CALL GetLabelFromType 0 (type wheelShopSelected)(pLabel)
                IF CLEO_CALL LabelNotEmpty 0 (pLabel)
                    BREAK
                ENDIF
            ENDWHILE
        ENDIF

        IF type < 0 // color or paintjob
            IF IS_KEY_PRESSED VK_KEY_Y
            AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD

                IF type = -2 // Color
                    USE_TEXT_COMMANDS 0
                    CLEO_CALL ChooseColors 0 (hVeh)
                    CLEO_CALL CountNewInstalledColors 0 (hVeh)(colorCost)
                    colorCost *= 150
                ENDIF

                IF type = -1 // Paintjob
                    GET_NUM_AVAILABLE_PAINTJOBS hVeh i
                    IF i > 0
                        USE_TEXT_COMMANDS 0
                        CLEO_CALL ChoosePaintjob 0 (hVeh installedPaintjob)
                        paintjobCost = 0
                        GET_CURRENT_VEHICLE_PAINTJOB hVeh i
                        IF NOT i = installedPaintjob
                            paintjobCost += 500
                        ENDIF
                    ENDIF
                ENDIF
            ENDIF
        ELSE
            CLEO_CALL FixedGetCurrentCarMod 0 (hVeh type)(curModel)
            IF type = UPGRADE_ALLWHEELS
                CLEO_CALL GetWheelMajorTextWidth 0 ()(majorSlotWidth)
            ENDIF
            IF type = UPGRADE_ALLWHEELS
            AND wheelShopSelected < 0
                IF IS_KEY_PRESSED VK_KEY_Y
                AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD
                    SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD
                    wheelShopSelected = slot
                    slot = 0
                    CLEO_CALL GetLabelFromType 0 (type wheelShopSelected)(pLabel)
                ENDIF
            ELSE
                CLEO_CALL GetLabelFromType 0 (type wheelShopSelected)(pLabel)
                CLEO_CALL GetMajorSlotTextWidth 0 (pLabel)(majorSlotWidth)
                CLEO_CALL GetUpgradeIDFromMemorySlot 0 (pLabel slot)(nModel)
                IF IS_KEY_PRESSED VK_KEY_Y
                AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD
                    SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD
                    IF curModel = nModel
                        REMOVE_VEHICLE_MOD hVeh nModel
                        IF CLEO_CALL ThisModelIsOriginallyInstalled 0 (curModel)
                        ELSE
                            GET_PRICE_OF_ITEM curModel (itemPrice)
                            totalCost -= itemPrice
                        ENDIF
                    ELSE
                        IF curModel > 0
                            IF CLEO_CALL ThisModelIsOriginallyInstalled 0 (curModel)
                            ELSE
                                GET_PRICE_OF_ITEM curModel (itemPrice)
                                totalCost -= itemPrice
                            ENDIF
                        ENDIF
                        REQUEST_VEHICLE_MOD nModel
                        LOAD_ALL_MODELS_NOW
                        ADD_VEHICLE_MOD hVeh nModel i
                        MARK_VEHICLE_MOD_AS_NO_LONGER_NEEDED nModel
                        IF CLEO_CALL ThisModelIsOriginallyInstalled 0 (nModel)
                        ELSE
                            GET_PRICE_OF_ITEM nModel (itemPrice)
                            totalCost += itemPrice
                        ENDIF
                    ENDIF
                ENDIF
            ENDIF
        ENDIF

        IF NOT IS_KEY_PRESSED VK_KEY_Y
        AND NOT IS_KEY_PRESSED VK_KEY_N
            CLEAR_LOCAL_VAR_BIT_CONST flags DISABLE_CMD
        ENDIF

        IF NOT IS_KEY_PRESSED VK_LEFT
        AND NOT IS_KEY_PRESSED VK_RIGHT
        AND NOT IS_KEY_PRESSED VK_UP
        AND NOT IS_KEY_PRESSED VK_DOWN
            CLEAR_LOCAL_VAR_BIT_CONST flags DISABLE_CMD_ARROW
        ENDIF

        IF IS_KEY_PRESSED VK_KEY_N
        AND NOT IS_LOCAL_VAR_BIT_SET_CONST flags DISABLE_CMD
            IF wheelShopSelected >= 0
                slot = wheelShopSelected
                wheelShopSelected = -1
                SET_LOCAL_VAR_BIT_CONST flags DISABLE_CMD
                CONTINUE
            ENDIF
            WHILE IS_KEY_PRESSED VK_KEY_N
                GOSUB ShowInterface
            ENDWHILE
            totalCost += colorCost
            totalCost += paintjobCost
            IF totalCost > 0
                totalCost *= -1
                ADD_SCORE 0 totalCost
                IF paintjobCost > 0
                OR colorCost > 0
                    CLEAR_WANTED_LEVEL_IN_GARAGE
                    SET_VEHICLE_DIRT_LEVEL hVeh 0.0
                ENDIF
                tempx =# totalCost
                INCREMENT_FLOAT_STAT 55 tempx
            ENDIF
            USE_TEXT_COMMANDS 0
            WRITE_MEMORY pExternalLabel 4 1 FALSE
            BREAK
        ENDIF
        
        GOSUB ShowInterface
    ENDWHILE

    //CLEO_CALL FreeMemory 0
    TERMINATE_THIS_CUSTOM_SCRIPT

    Test:
    DUMP
    00 00 00 00
    ENDDUMP

///////////////////////////////////////////////////////////////////////////////////////////////////

    ShowInterface:
    WAIT 0
    
    USE_TEXT_COMMANDS 1

    SET_SPRITES_DRAW_BEFORE_FADE ON
    DRAW_SPRITE SPRITE_BLANK (320.0 20.0)(640.0 60.0)(0 0 0 180)
    //SET_SPRITES_DRAW_BEFORE_FADE ON
    DRAW_RECT (320.0 50.0)(640.0 2.0)(200 200 200 255)

    // Show money
    STORE_SCORE 0 i
    GOSUB FormatMoney
    DISPLAY_TEXT_WITH_NUMBER (630.0 54.0) _TM_07 i

    i = totalCost + colorCost
    i += paintjobCost
    GOSUB FormatMoney
    SET_TEXT_COLOUR 255 100 100 255
    DISPLAY_TEXT_WITH_NUMBER (630.0 66.0) _TM_07 i
    
    x = 23.0
    y = 25.0
    k = -1
    WHILE k <= 18
        j = k - 1
        CLEO_CALL GetLabelFromType 0 (j wheelShopSelected)(pLabel)
        IF j = type
            IF type > 0
                GOSUB ProcessSlots
            ENDIF
            opacity = 255
            sx = 34.5
            sy = 40.0
        ELSE
            opacity = 80
            sx = 33.0
            sy = 38.0
        ENDIF
        IF k = -1 // color
            SET_SPRITES_DRAW_BEFORE_FADE ON
            DRAW_SPRITE SPRITE_TUNINGC (x y)(sx sy)(255 255 255 opacity)
            CLEO_CALL CountNewInstalledColors 0 (hVeh)(j)
            IF j > 0
                tempx = x
                tempx -= 12.0
                slotsPosY = y
                slotsPosY += 14.0
                SET_SPRITES_DRAW_BEFORE_FADE ON
                DRAW_SPRITE SPRITE_SHOPCART (tempx slotsPosY) (10.0 8.0) (200 200 200 opacity)
            ENDIF
        ELSE
            IF k = 0 // paintjob
                GET_NUM_AVAILABLE_PAINTJOBS hVeh j
                IF j > 0
                    SET_SPRITES_DRAW_BEFORE_FADE ON
                    DRAW_SPRITE SPRITE_TUNINGP (x y)(sx sy)(255 255 255 opacity)
                    GET_CURRENT_VEHICLE_PAINTJOB hVeh j
                    IF NOT j = installedPaintjob
                        tempx = x
                        tempx -= 12.0
                        slotsPosY = y
                        slotsPosY += 14.0
                        SET_SPRITES_DRAW_BEFORE_FADE ON
                        DRAW_SPRITE SPRITE_SHOPCART (tempx slotsPosY) (10.0 8.0) (200 200 200 opacity)
                    ENDIF
                    IF j >= 0
                        tempx = x
                        tempx += 12.0
                        slotsPosY = y
                        slotsPosY += 14.0
                        SET_SPRITES_DRAW_BEFORE_FADE ON
                        DRAW_SPRITE SPRITE_CAR (tempx slotsPosY) (10.0 8.0) (200 200 200 opacity)
                    ENDIF
                ELSE
                    k++
                    CONTINUE
                ENDIF
            ELSE
                IF CLEO_CALL LabelNotEmpty 0 (pLabel)
                    IF k = 12 // unknown
                    OR k = 17 // misc
                        SET_SPRITES_DRAW_BEFORE_FADE ON
                        DRAW_SPRITE SPRITE_TUNINGO (x y)(sx sy)(255 255 255 opacity)
                    ELSE
                        SET_SPRITES_DRAW_BEFORE_FADE ON
                        IF k = 18 // all wheels
                            GET_CAR_MODEL hVeh j
                            IF NOT IS_THIS_MODEL_A_BOAT J
                                DRAW_SPRITE SPRITE_TUNING12 (x y)(sx sy)(255 255 255 opacity)
                            ENDIF
                        ELSE
                            DRAW_SPRITE k (x y)(sx sy)(255 255 255 opacity)
                        ENDIF
                    ENDIF
                ELSE
                    k++
                    CONTINUE
                ENDIF
            ENDIF
        ENDIF
        x += 35.0
        k++
    ENDWHILE

    USE_TEXT_COMMANDS 0
    RETURN


    ProcessSlots:
    CLEO_CALL CountSlots 0 (pLabel type wheelShopSelected)(j)
    slotsSizeY =# j
    slotsSizeY *= 20.0 // y of each item
    slotsSizeY += 20.0 // margin bottom
    //slotsPosY = slotsSizeY / 2.0
    //slotsPosY += 50.0 // padding top
    i = 0
    slotsPosY = 62.0

    slotsPosX = x

    tempx = majorSlotWidth
    tempx /= 2.0
    tempx += x
    IF tempx > 640.0
        tempx -= 640.0
        slotsPosX -= tempx
    ENDIF

    WHILE i < j

        IF i = slot
            opacity = 255
        ELSE
            opacity = 180
        ENDIF
        DRAW_RECT (slotsPosX slotsPosY)(majorSlotWidth 25.0)(0 0 0 opacity)

        // Show item name
        IF k = 18
        AND NOT wheelShopSelected >= 0
            SWITCH i
                CASE 0
                    itemName = CARMOD1 // Transfender
                    BREAK
                CASE 1
                    itemName = CARMOD3 // Loco Low Co
                    BREAK
                CASE 2
                    itemName = CARMOD2 // Wheel Arch Angels
                    BREAK
                CASE 3
                    itemName = _TM_640 // More...
                    BREAK
            ENDSWITCH
            slotsPosY -= 5.0
            GOSUB FormatSlotItem
            DISPLAY_TEXT (slotsPosX slotsPosY) $itemName
            slotsPosY += 5.0
        ELSE
            CLEO_CALL GetUpgradeIDFromMemorySlot 0 (pLabel i)(nModel)
            GET_NAME_OF_ITEM nModel (itemName)
            slotsPosY -= 10.0
            GOSUB FormatSlotItem
            IF curModel = nModel // currently installed
                SET_TEXT_COLOUR 160 160 160 120
            ENDIF
            DISPLAY_TEXT (slotsPosX slotsPosY) $itemName

            slotsPosY += 15.0

            GET_PRICE_OF_ITEM nModel (itemPrice)
            GOSUB FormatSlotPrice
            IF CLEO_CALL ThisModelIsOriginallyInstalled 0 (nModel)
                tempx = majorSlotWidth
                tempx /= 2.0
                tempx -= 6.5 // right margin
                tempx += x
                DRAW_SPRITE SPRITE_CAR (tempx slotsPosY) (10.0 8.0) (200 200 200 255)
                SET_TEXT_COLOUR 160 160 160 120
                slotsPosY -= 5.0
            ELSE
                slotsPosY -= 5.0
                IF curModel = nModel // currently installed
                    tempx = 0.0
                    tempx -= majorSlotWidth
                    tempx /= 2.0
                    tempx += 6.5 // left margin
                    tempx += x
                    slotsPosY += 5.0
                    DRAW_SPRITE SPRITE_SHOPCART (tempx slotsPosY) (10.0 8.0) (200 200 200 255)
                    slotsPosY -= 5.0
                ENDIF
            ENDIF
            DISPLAY_TEXT_WITH_NUMBER (slotsPosX slotsPosY) _TM_07 itemPrice
        ENDIF


        // Next line
        slotsPosY += 25.0
        i++
    ENDWHILE
    RETURN


    FormatMoney:
    SET_TEXT_FONT 1
    SET_TEXT_SCALE 0.3 1.1
    SET_TEXT_RIGHT_JUSTIFY ON
    SET_TEXT_WRAPX 640.0
    SET_TEXT_COLOUR 100 200 100 255
    SET_TEXT_EDGE 1 0 0 0 200
    RETURN

    FormatSlotItem:
    SET_TEXT_FONT 1
    SET_TEXT_SCALE 0.22 1.02
    SET_TEXT_CENTRE ON
    SET_TEXT_CENTRE_SIZE 640.0
    SET_TEXT_COLOUR 200 200 200 opacity
    SET_TEXT_EDGE 1 0 0 0 200
    RETURN

    FormatSlotPrice:
    SET_TEXT_FONT 1
    SET_TEXT_SCALE 0.22 1.02
    SET_TEXT_CENTRE ON
    SET_TEXT_CENTRE_SIZE 640.0
    SET_TEXT_COLOUR 100 200 100 opacity
    SET_TEXT_EDGE 1 0 0 0 200
    RETURN


    LoadFiles:
    LOAD_TEXTURE_DICTIONARY TUNINGM
    LOAD_SPRITE 1 TUNING0
    LOAD_SPRITE 2 TUNING1
    LOAD_SPRITE 3 TUNING2
    LOAD_SPRITE 4 TUNING3
    LOAD_SPRITE 5 TUNING4
    LOAD_SPRITE 6 TUNING5
    LOAD_SPRITE 7 TUNING6
    LOAD_SPRITE 8 TUNING7
    LOAD_SPRITE 9 TUNING8
    LOAD_SPRITE 10 TUNING9
    LOAD_SPRITE 11 TUNING10
    //LOAD_SPRITE 12 TUNING11
    LOAD_SPRITE 13 TUNING12
    LOAD_SPRITE 14 TUNING13
    LOAD_SPRITE 15 TUNING14
    LOAD_SPRITE 16 TUNING15
    LOAD_SPRITE 17 TUNINGC
    LOAD_SPRITE 18 TUNINGP
    LOAD_SPRITE 19 TUNINGO
    LOAD_SPRITE 20 BLANK
    LOAD_SPRITE 21 SHOPCART
    LOAD_SPRITE 22 CAR
    LOAD_SPRITE 23 DEFAULT
    RETURN
}
SCRIPT_END

///////////////////////////////////////////////////////////////////////////////////////////////////
/*
{
    LVAR_INT veh frame id
    show:
    GET_VEHICLE_POINTER veh veh
    veh += 0x18
    READ_MEMORY veh 4 FALSE (veh)
    WHILE TRUE
        WAIT 0
        IF IS_KEY_PRESSED 107
            IF IS_KEY_PRESSED VK_LSHIFT
                id += 10
            ELSE
                id++
            ENDIF
            WHILE IS_KEY_PRESSED 107
                wait 0
            ENDWHILE
        ENDIF
        IF IS_KEY_PRESSED 109
            IF IS_KEY_PRESSED VK_LSHIFT
                id -= 10
            ELSE
                id--
            ENDIF
            WHILE IS_KEY_PRESSED 109
                wait 0
            ENDWHILE
        ENDIF
        CALL_FUNCTION_RETURN 0x4C53C0 2 2 (id veh)(frame)
        PRINT_FORMATTED_NOW "%d %x" 100 id frame
    ENDWHILE
    CLEO_RETURN 0
}
*/
{
    LVAR_INT hVeh type // In
    LVAR_INT curModel id clump frame pLabel
    FixedGetCurrentCarMod:
    IF type >= 12
        IF type = 17 //internal
            type = 12
        ENDIF
        SWITCH type
            CASE 12
                id = 2
                BREAK
            CASE 13
                id = 19
                BREAK
            CASE 14
                id = 12
                BREAK
            CASE 15
                id = 13
                BREAK
            CASE 16
                id = 20
                BREAK
        ENDSWITCH
        GET_VEHICLE_POINTER hVeh clump
        clump += 0x18
        READ_MEMORY clump 4 FALSE (clump)
        CALL_FUNCTION_RETURN 0x004C53C0 2 2 (id clump)(frame) //_rpClumpFindFrameById
        IF NOT frame > 0
            CLEO_RETURN 0 (-1)
        ENDIF
    ENDIF
    GET_CURRENT_CAR_MOD hVeh type curModel
    CLEO_RETURN 0 (curModel)
}

{
    LVAR_INT nModel // In
    LVAR_INT type value pLabel
    ThisModelIsOriginallyInstalled:
    GET_LABEL_POINTER InstalledParts pLabel
    REPEAT 16 type
        READ_MEMORY pLabel 2 FALSE value
        IF value = nModel
            RETURN_TRUE
            CLEO_RETURN 0
        ENDIF
        pLabel += 0x2
    ENDREPEAT
    RETURN_FALSE
    CLEO_RETURN 0
}

{
    LVAR_INT color // In
    LVAR_INT colorId value pLabel
    ThisColorIsOriginallyInstalled:
    GET_LABEL_POINTER InstalledColors pLabel
    REPEAT 4 colorId
        READ_MEMORY pLabel 1 FALSE value
        IF value = color
            RETURN_TRUE
            CLEO_RETURN 0
        ENDIF
        pLabel += 0x1
    ENDREPEAT
    RETURN_FALSE
    CLEO_RETURN 0
}

{
    LVAR_INT color slot // In
    LVAR_INT colorId value pLabel
    ThisColorIsOriginallyInstalledInSlot:
    GET_LABEL_POINTER InstalledColors pLabel
    pLabel += slot
    READ_MEMORY pLabel 1 FALSE value
    IF value = color
        RETURN_TRUE
        CLEO_RETURN 0
    ENDIF
    RETURN_FALSE
    CLEO_RETURN 0
}

{
    LVAR_INT hVeh // In
    LVAR_INT colorId colors[4] installedColors value pLabel
    CountNewInstalledColors:
    GET_CAR_COLOURS hVeh colors[0] colors[1] 
    GET_EXTRA_CAR_COLOURS hVeh colors[2] colors[3] 
    REPEAT 4 colorId
        IF CLEO_CALL ThisColorIsOriginallyInstalled 0 (colors[colorId])
        ELSE
            installedColors++
        ENDIF
    ENDREPEAT
    CLEO_RETURN 0 (installedColors)
}

{
    LVAR_INT hVeh // In
    LVAR_INT type nModel pLabel
    StoreAllInstalledParts:
    GET_LABEL_POINTER InstalledParts pLabel
    REPEAT 16 type
        CLEO_CALL FixedGetCurrentCarMod 0 (hVeh type)(nModel)
        WRITE_MEMORY pLabel 2 nModel FALSE
        pLabel += 0x2
    ENDREPEAT
    CLEO_RETURN 0
}

{
    LVAR_INT hVeh // In
    LVAR_INT colorId colors[4] pLabel
    StoreAllInstalledColors:
    GET_LABEL_POINTER InstalledColors pLabel
    GET_CAR_COLOURS hVeh colors[0] colors[1] 
    GET_EXTRA_CAR_COLOURS hVeh colors[2] colors[3] 
    REPEAT 4 colorId
        WRITE_MEMORY pLabel 1 colors[colorId] FALSE
        pLabel += 0x1
    ENDREPEAT
    CLEO_RETURN 0
}

{
    LVAR_INT label // In
    LVAR_INT iWidth nModel
    LVAR_FLOAT fWidth fMajorWidth
    LVAR_TEXT_LABEL itemName
    GetMajorSlotTextWidth:
    WHILE TRUE
        READ_MEMORY label 2 FALSE (nModel)
        IF nModel > 0
            GET_NAME_OF_ITEM nModel (itemName)
            GET_STRING_WIDTH $itemName (iWidth)
            fWidth =# iWidth
            IF fWidth > fMajorWidth
                fMajorWidth = fWidth
            ENDIF
        ELSE
            BREAK
        ENDIF
        label += 0x2
    ENDWHILE
    fMajorWidth /= 2.0
    fMajorWidth += 10.0
    IF fMajorWidth < 70.0
        fMajorWidth = 70.0
    ENDIF
    CLEO_RETURN 0 (fMajorWidth)
}

{
    LVAR_INT iWidth nModel
    LVAR_FLOAT fWidth fMajorWidth
    LVAR_TEXT_LABEL itemName
    GetWheelMajorTextWidth:
    GET_STRING_WIDTH CARMOD1 (iWidth)
    GOSUB GetWheelMajorTextWidth_UpdateMajor
    GET_STRING_WIDTH CARMOD2 (iWidth)
    GOSUB GetWheelMajorTextWidth_UpdateMajor
    GET_STRING_WIDTH CARMOD3 (iWidth)
    GOSUB GetWheelMajorTextWidth_UpdateMajor
    GET_STRING_WIDTH _TM_640 (iWidth)
    GOSUB GetWheelMajorTextWidth_UpdateMajor
    fMajorWidth /= 2.0
    fMajorWidth += 10.0
    IF fMajorWidth < 70.0
        fMajorWidth = 70.0
    ENDIF
    CLEO_RETURN 0 (fMajorWidth)

    GetWheelMajorTextWidth_UpdateMajor:
    fWidth =# iWidth
    IF fWidth > fMajorWidth
        fMajorWidth = fWidth
    ENDIF
    RETURN
}

{
    LVAR_INT label type nShop // In
    LVAR_INT totalSlots value i j
    CountSlots:
    IF type = UPGRADE_ALLWHEELS
    AND nShop < 0
        REPEAT 4 i
            CLEO_CALL GetLabelFromType 0 (17 i)(label)
            IF CLEO_CALL LabelNotEmpty 0 (label)
                totalSlots++
            ENDIF
        ENDREPEAT
        CLEO_RETURN 0 (totalSlots)
    ENDIF
    WHILE TRUE
        READ_MEMORY label 2 FALSE (value)
        IF value > 0
            totalSlots++
        ELSE
            BREAK
        ENDIF
        label += 0x2
    ENDWHILE
    CLEO_RETURN 0 (totalSlots)
}

{
    LVAR_INT label //In
    LVAR_INT value
    LabelNotEmpty:
    READ_MEMORY label 2 FALSE (value)
    IS_THING_GREATER_THAN_THING value 0
    CLEO_RETURN 0
}

{
    LVAR_INT label slot //In
    LVAR_INT id offset
    GetUpgradeIDFromMemorySlot:
    offset = slot * 2
    offset += label
    READ_MEMORY offset 2 FALSE (id)
    CLEO_RETURN 0 (id)
}

{
    LVAR_INT memory
    FreeMemory:
    GET_LABEL_POINTER TMemory_Start (memory)
    WRITE_MEMORY memory 400 (0) FALSE
    CLEO_RETURN 0
}

{
    LVAR_INT nVehModelID // In
    
    StoreAllUpgradesToCar:
    LVAR_INT i nModel nVehModType pVehModelInfo pVehMods pLabel nMemorySlot offset value

    CALL_FUNCTION_RETURN CModelInfo__GetModelInfo 1 1 (nVehModelID)(pVehModelInfo)
    pVehMods = pVehModelInfo + 0x2D6 // carMods

    REPEAT 18 i
        READ_MEMORY pVehMods 2 FALSE (nModel)
        IF nModel = 0xFFFF 
            BREAK
        ENDIF

        // Store upgrade by type
        GET_VEHICLE_MOD_TYPE nModel (nVehModType)
        IF NOT nVehModType = UPGRADE_WHEELS
            CLEO_CALL GetLabelFromType 0 (nVehModType -1)(pLabel)

            nMemorySlot = 0
            WHILE nMemorySlot <= 8
                offset = nMemorySlot * 2
                offset += pLabel
                READ_MEMORY offset 2 FALSE (value)
                IF value = 0
                    WRITE_MEMORY offset 2 (nModel) FALSE
                    BREAK
                ENDIF
                nMemorySlot++
            ENDWHILE
        ENDIF

        pVehMods += 0x2
    ENDREPEAT
    CLEO_RETURN 0
}

{
    LVAR_INT nVehModType nShop //In
    LVAR_INT pLabel

    GetLabelFromType:
    IF nShop >= 0
    AND nVehModType = UPGRADE_ALLWHEELS
        SWITCH nShop
            CASE WHEEL_SET_TF
                GET_LABEL_POINTER TMemory_Wheels_TF (pLabel)
                BREAK
            CASE WHEEL_SET_WAA
                GET_LABEL_POINTER TMemory_Wheels_WAA (pLabel)
                BREAK
            CASE WHEEL_SET_LLC
                GET_LABEL_POINTER TMemory_Wheels_LLC (pLabel)
                BREAK
            CASE WHEEL_SET_MORE
                GET_LABEL_POINTER TMemory_Wheels_MORE (pLabel)
                BREAK
        ENDSWITCH
        CLEO_RETURN 0 (pLabel)
    ENDIF
    IF nVehModType < 0
        CLEO_RETURN 0 (-1)
    ENDIF
    SWITCH nVehModType
        CASE UPGRADE_HOOD
            GET_LABEL_POINTER TMemory_HOOD (pLabel)
            BREAK
        CASE UPGRADE_VENT
            GET_LABEL_POINTER TMemory_VENT (pLabel)
            BREAK
        CASE UPGRADE_SPOIL
            GET_LABEL_POINTER TMemory_SPOIL (pLabel)
            BREAK
        CASE UPGRADE_SIDESKIRT
            GET_LABEL_POINTER TMemory_SIDESKIRT (pLabel)
            BREAK
        CASE UPGRADE_BULLFRNT
            GET_LABEL_POINTER TMemory_BULLFRNT (pLabel)
            BREAK
        CASE UPGRADE_BULLREAR
            GET_LABEL_POINTER TMemory_BULLREAR (pLabel)
            BREAK
        CASE UPGRADE_LIGHTS
            GET_LABEL_POINTER TMemory_LIGHTS (pLabel)
            BREAK
        CASE UPGRADE_ROOF
            GET_LABEL_POINTER TMemory_ROOF (pLabel)
            BREAK
        CASE UPGRADE_NITRO
            GET_LABEL_POINTER TMemory_NITRO (pLabel)
            BREAK
        CASE UPGRADE_HYDRAULICS
            GET_LABEL_POINTER TMemory_HYDRAULICS (pLabel)
            BREAK
        CASE UPGRADE_STEREO
            GET_LABEL_POINTER TMemory_STEREO (pLabel)
            BREAK
        CASE UPGRADE_WHEELS
            GET_LABEL_POINTER TMemory_WHEELS (pLabel)
            BREAK
        CASE UPGRADE_EXHAUST
            GET_LABEL_POINTER TMemory_EXHAUST (pLabel)
            BREAK
        CASE UPGRADE_BUMPFRNT
            GET_LABEL_POINTER TMemory_BUMPFRNT (pLabel)
            BREAK
        CASE UPGRADE_BUMPREAR
            GET_LABEL_POINTER TMemory_BUMPREAR (pLabel)
            BREAK
        CASE UPGRADE_MISC
            GET_LABEL_POINTER TMemory_MISC (pLabel)
            BREAK
        CASE UPGRADE_ALLWHEELS
            GET_LABEL_POINTER TMemory_ALLWHEELS (pLabel)
            BREAK
        DEFAULT
            GET_LABEL_POINTER TMemory_MISC (pLabel)
            BREAK
    ENDSWITCH
    CLEO_RETURN 0 (pLabel)
}

{
    LVAR_INT nTotal i pLabel wheel[15]

    StoreAllWheels:

    REPEAT 4 i
        CLEO_CALL GetLabelFromType 0 (17 i)(pLabel)
        CLEO_CALL StoreWheelsInSet 0 (i pLabel)()
    ENDREPEAT

    CLEO_RETURN 0
}

{
    LVAR_INT nSetID pLabel // In
    LVAR_INT nTotal x pSet wheelId

    StoreWheelsInSet:
    pSet = nSetID * 0x1E
    pSet = pSet + 0x00B4E3F8

    nSetID = nSetID * 2
    nSetID = nSetID + 0x00B4E470

    READ_MEMORY nSetID 2 FALSE nTotal

    IF NOT nTotal = 0
        x = 0
        WHILE x < nTotal
            READ_MEMORY pSet 2 FALSE wheelId
            WRITE_MEMORY pLabel 2 wheelId FALSE
            pSet += 0x2
            pLabel += 0x2
            x++
        ENDWHILE
    ENDIF
    CLEO_RETURN 0
}

///////////////////////////////////////////////////////////////////////////////////////////////////

{
    LVAR_INT hVeh installedPaintjob // In
    LVAR_INT totalPaintjobs maxSpriteId selected originalPaintjob i j forceColorWhite curSpriteId primaryColor secondaryColor
    LVAR_FLOAT f posx posy
    ChoosePaintjob:
    DO_FADE 0 100
    WAIT 150
    WHILE IS_KEY_PRESSED VK_KEY_Y
        WAIT 0
    ENDWHILE
    DO_FADE 1 150

    CONST_FLOAT fSize 150.0

    GET_NUM_AVAILABLE_PAINTJOBS hVeh totalPaintjobs
    maxSpriteId = totalPaintjobs
    maxSpriteId += 100

    IF NOT totalPaintjobs > 0
        CLEO_RETURN 0
    ENDIF

    selected = 0
    GET_CURRENT_VEHICLE_PAINTJOB hVeh originalPaintjob
    selected = originalPaintjob + 1 // set current selected

    i = 1
    WHILE i <= totalPaintjobs
        CLEO_CALL Load_Paintjob_As_Sprite 0 (hVeh i)
        i++
    ENDWHILE

    IF CLEO_CALL CheckIFPaintjobColorWhiteDisabled 0
        forceColorWhite = false
    ELSE
        forceColorWhite = true
    ENDIF
    GET_CAR_COLOURS hVeh primaryColor secondaryColor

    WHILE TRUE
        GOSUB ChoosePaintjob_Interface
        
        IF IS_KEY_PRESSED VK_RIGHT
            selected++           
            IF selected > totalPaintjobs
            selected = 0
            ENDIF    
            GOSUB ChoosePaintjob_Set 
            WHILE IS_KEY_PRESSED VK_RIGHT
                GOSUB ChoosePaintjob_Interface
            ENDWHILE 
        ENDIF  
        IF IS_KEY_PRESSED VK_LEFT
            selected--              
            IF selected < 0
            selected = totalPaintjobs
            ENDIF
            GOSUB ChoosePaintjob_Set
            WHILE IS_KEY_PRESSED VK_LEFT
                GOSUB ChoosePaintjob_Interface
            ENDWHILE    
        ENDIF   
        IF IS_KEY_PRESSED VK_KEY_B
            IF forceColorWhite = true
                forceColorWhite = false
                GOSUB ChoosePaintjob_ResetCarColor
            ELSE
                forceColorWhite = true  
                GOSUB ChoosePaintjob_SetCarColorWhite   
            ENDIF
            WHILE IS_KEY_PRESSED VK_KEY_B
                GOSUB ChoosePaintjob_Interface
            ENDWHILE  
        ENDIF 
        
        IF IS_KEY_PRESSED VK_KEY_Y
            BREAK
        ENDIF 
        IF IS_KEY_PRESSED VK_KEY_N
            GOSUB ChoosePaintjob_Set
            GIVE_VEHICLE_PAINTJOB hVeh originalPaintjob
            BREAK
        ENDIF
    ENDWHILE

    DO_FADE 0 100
    WAIT 100
    USE_TEXT_COMMANDS 0

    i = 1
    WHILE i <= totalPaintjobs
        CLEO_CALL Get_TXD_Index 0 (hVeh i)(j)
        CALL_FUNCTION 0x731A30 1 1 (j) // RemoveRef   
        CLEO_CALL Unload_Paintjob_As_Sprite 0 (hVeh i)()
        i++
    ENDWHILE

    WHILE IS_KEY_PRESSED VK_KEY_Y
        WAIT 0
    ENDWHILE 
    WHILE IS_KEY_PRESSED VK_KEY_N
        WAIT 0
    ENDWHILE
    DO_FADE 1 150
    CLEO_RETURN 0

/////////////////////////////////////////////
    
    ChoosePaintjob_Interface:
    WAIT 0

    // Selection
    IF totalPaintjobs > 5 // IF some mod increases the max paintjobs
        posx = 60.0
    ELSE
        f =# totalPaintjobs
        f *= 30.0
        posx = 300.0
        posx -= f
    ENDIF  
    posy = 60.0
    curSpriteId = 100
    WHILE curSpriteId <= maxSpriteId
        i = curSpriteId - 100
        IF selected = i
            //CLEO_CALL GUI_DrawBoxOutline_WithText 20 PosX posx PosY posy SizeX 52.0 SizeY 62.0 RGBA 0 0 0 0 OutlineSize 2.0 OutlineSides 1 1 1 1 OutlineRGBA -2 -2 -2 -2 TextID -1     FormatID -1     Padding -1
            SET_SPRITES_DRAW_BEFORE_FADE TRUE
            DRAW_RECT posx posy 55.0 65.0 (255 255 255 255)
        ENDIF
        
        SET_SPRITES_DRAW_BEFORE_FADE TRUE
        IF curSpriteId = 100
            DRAW_SPRITE SPRITE_DEFAULT posx posy 50.0 60.0 (255 255 255 255)
        ELSE
            IF selected = i
                DRAW_SPRITE curSpriteId posx posy 52.0 62.0 (255 255 255 255)
            ELSE
                DRAW_SPRITE curSpriteId posx posy 50.0 60.0 (255 255 255 255)
            ENDIF
        ENDIF

        i--
        IF installedPaintjob = i
            posy += 38.0
            DRAW_SPRITE SPRITE_CAR (posx posy) (10.0 8.0) (200 200 200 255)
            posy -= 38.0
        ENDIF
        i++
        
        posx += 60.0
        IF i = 10 
        OR i = 20
            posx = 60.0
            posy += 70.0
        ENDIF

        curSpriteId++
    ENDWHILE


    //Footer
    //CLEO_CALL GUI_DrawBoxOutline_WithText 20 PosX 320.0 PosY 440.0 SizeX 640.0 SizeY 55.0 RGBA 0 0 0 255 OutlineSize 1.4 OutlineSides 1 0 0 0 OutlineRGBA 200 200 200 200 TextID -1     FormatID -1     Padding -1
    DRAW_RECT 320.0 440.0 640.0 55.0 (0 0 0 255)
    DRAW_RECT 320.0 412.0 640.0 1.2 (255 255 255 255)

    GOSUB FormatTip            
    IF forceColorWhite = true
        DISPLAY_TEXT 30.0 390.0 _TM_631
    ELSE
        DISPLAY_TEXT 30.0 390.0 _TM_632
    ENDIF

    GOSUB FormatDescription
    DISPLAY_TEXT 320.0 425.0 _TM_177

    //CLEO_CALL MouseCamera 2 hVeh -1

    //CLEO_CALL GUI_ItemMenuActive_PulseColor_Update 0 
        
    USE_TEXT_COMMANDS 0
    RETURN


    FormatDescription:
    SET_TEXT_FONT 1
    SET_TEXT_SCALE 0.22 1.02
    SET_TEXT_CENTRE ON
    SET_TEXT_CENTRE_SIZE 640.0
    SET_TEXT_COLOUR 200 200 200 255
    SET_TEXT_EDGE 1 0 0 0 200
    RETURN

    FormatTip:
    SET_TEXT_FONT 1
    SET_TEXT_SCALE 0.22 1.02
    SET_TEXT_WRAPX 640.0
    SET_TEXT_COLOUR 200 200 200 255
    SET_TEXT_EDGE 1 0 0 0 200
    RETURN


    ChoosePaintjob_Set:
    i = selected + -1
    GIVE_VEHICLE_PAINTJOB hVeh i
    IF forceColorWhite = true
        GOSUB ChoosePaintjob_SetCarColorWhite
    ELSE
        GOSUB ChoosePaintjob_ResetCarColor
    ENDIF
    RETURN

    ChoosePaintjob_SetCarColorWhite:
    GOSUB EnablePaintjobColorWhite
    CHANGE_CAR_COLOUR hVeh 1 secondaryColor
    RETURN

    ChoosePaintjob_ResetCarColor:
    GOSUB DisablePaintjobColorWhite  
    CHANGE_CAR_COLOUR hVeh primaryColor secondaryColor
    RETURN

    EnablePaintjobColorWhite:
    WRITE_MEMORY 0x6D65C5 1 0xA8 TRUE
    WRITE_MEMORY 0x6D65C6 1 0x4 TRUE
    WRITE_MEMORY 0x6D65C7 1 0x75 TRUE
    WRITE_MEMORY 0x6D65C8 1 0x22 TRUE
    WRITE_MEMORY 0x6D65C9 1 0xC6 TRUE
    WRITE_MEMORY 0x6D65CA 1 0x86 TRUE
    WRITE_MEMORY 0x6D65CB 1 0x34 TRUE
    WRITE_MEMORY 0x6D65CC 1 0x4 TRUE
    WRITE_MEMORY 0x6D65CD 1 0x0 TRUE
    WRITE_MEMORY 0x6D65CE 1 0x0 TRUE
    WRITE_MEMORY 0x6D65CF 1 0x1 TRUE            
    RETURN

    DisablePaintjobColorWhite:   
    WRITE_MEMORY 0x6D65C5 11 0x90 TRUE
    RETURN

}

{
    LVAR_INT hVeh id
    LVAR_INT i
    Get_TXD_Index:
    GET_CAR_MODEL hVeh i
    CALL_FUNCTION_RETURN 0x403DA0 1 1 (i)(i)  // CBaseModelInfo
    i += 0x2FA   // CVehicleModelInfo->Paintjobs (dw 5 dup)
    id--
    id *= 0x2
    i += id
    READ_MEMORY i 2 FALSE (i)
    CLEO_RETURN 0 i
}

//by Kevin Prestes
{
    LVAR_INT hVeh id //In
    LVAR_INT i txd j p
    Load_Paintjob_As_Sprite:
    CLEO_CALL Get_TXD_Index 0 (hVeh id)(i)
    READ_MEMORY 0x407106 4 FALSE (txd)   // .txd offset
    txd += i

    REQUEST_MODEL txd
    LOAD_ALL_MODELS_NOW

    CALL_FUNCTION 0x731A00 1 1 (i)   // AddRef
    CALL_FUNCTION_RETURN 0x408340 1 1 (i) (i)   // retorna RwTexDictionary
    CALL_FUNCTION_RETURN 0x734940 1 1 (i) (i)   // GetFirstTexture - retorna RwTexture

    READ_MEMORY 0x4840DD 4 FALSE (p)  // Ponteiro para o inicio dos CTexture utilizados pelo script
    j = id
    j += 100 // start of free sprite ids on tuning mod
    j *= 0x4
    p += j
    WRITE_MEMORY p 4 i FALSE
    CLEO_RETURN 0
}

{
    LVAR_INT hVeh id //In
    LVAR_INT p j
    Unload_Paintjob_As_Sprite:
    READ_MEMORY 0x4840DD 4 FALSE (p)  // Ponteiro para o inicio dos CTexture utilizados pelo script
    j = id
    j += 100 // start of free sprite ids on tuning mod
    j *= 0x4
    p += j
    WRITE_MEMORY p 4 0 FALSE
    CLEO_RETURN 0
}


{   
    LVAR_INT i
    CheckIFPaintjobColorWhiteDisabled:
    READ_MEMORY 0x6D65C5 1 FALSE (i)
    IF i = 0x90
        RETURN_TRUE
    ELSE
        RETURN_FALSE
    ENDIF
    CLEO_RETURN 0
}


///////////////////////////////////////////////////////////////////////////////////////////////////
{
    LVAR_INT hVeh //In
    LVAR_INT curColors[4]
    LVAR_INT bakColors[4]
    LVAR_INT tempColors[4]
    LVAR_INT curIndex showingList i j curColorID r g b startColorId endColorId maxColors
    LVAR_FLOAT posX posY

    ChooseColors:
    DO_FADE 0 150
    WAIT 150
    DO_FADE 1 250
    GET_CAR_COLOURS hVeh curColors[0] curColors[1]
    GET_EXTRA_CAR_COLOURS hVeh curColors[2] curColors[3]
    bakColors[0] = curColors[0]
    bakColors[1] = curColors[1]
    bakColors[2] = curColors[2]
    bakColors[3] = curColors[3]
    
    READ_MEMORY 0x582220 1 FALSE (maxColors)
    IF maxColors = 128
        maxColors = 126 // fix default limit
    ENDIF

    WHILE TRUE
        GOSUB ChooseColors_Interface
        
        IF IS_KEY_PRESSED VK_RIGHT
            curIndex++   
            IF curIndex > 3
                curIndex = 0
            ENDIF   
            WHILE IS_KEY_PRESSED VK_RIGHT
                GOSUB ChooseColors_Interface
            ENDWHILE  
        ENDIF  
        IF IS_KEY_PRESSED VK_LEFT
            curIndex--   
            IF curIndex < 0
                curIndex = 3
            ENDIF  
            WHILE IS_KEY_PRESSED VK_LEFT
                GOSUB ChooseColors_Interface
            ENDWHILE
        ENDIF  
        
        IF IS_KEY_PRESSED 49
            curIndex = 0
        ENDIF  
        IF IS_KEY_PRESSED 50
            curIndex = 1
        ENDIF
        IF IS_KEY_PRESSED 51
            curIndex = 2
        ENDIF
        IF IS_KEY_PRESSED 52
            curIndex = 3
        ENDIF
        
        // Open color list
        IF IS_KEY_PRESSED VK_KEY_Y
        
            WHILE IS_KEY_PRESSED VK_KEY_Y
                GOSUB ChooseColors_Interface
            ENDWHILE

            showingList = 1
            tempColors[0] = curColors[0]
            tempColors[1] = curColors[1]
            tempColors[2] = curColors[2]
            tempColors[3] = curColors[3]
            curColorID = curColors[curIndex]

            WHILE TRUE  
                GOSUB ChooseColors_Interface
                
                IF timera > 10
                
                    IF IS_KEY_PRESSED VK_UP
                        IF IS_KEY_PRESSED VK_LSHIFT
                            curColorID -= 2
                        ELSE 
                            curColorID--
                        ENDIF
                        timera = 0
                    ENDIF  
                    IF IS_KEY_PRESSED VK_DOWN
                        IF IS_KEY_PRESSED VK_LSHIFT
                            curColorID += 2
                        ELSE 
                            curColorID++
                        ENDIF
                        timera = 0
                    ENDIF
                ENDIF

                IF curColorID > maxColors
                    curColorID = 0
                ENDIF   
                IF curColorID < 0
                    curColorID = maxColors
                ENDIF
                
                
                curColors[curIndex] = curColorID
                
                GOSUB ChooseColors_ShowAllColors
                
                GOSUB ChooseColors_Set
                
                IF IS_KEY_PRESSED VK_KEY_Y
                    showingList = 0
                    GET_CAR_COLOURS hVeh curColors[0] curColors[1]
                    GET_EXTRA_CAR_COLOURS hVeh curColors[2] curColors[3]
                    WHILE IS_KEY_PRESSED VK_KEY_Y
                        GOSUB ChooseColors_Interface
                    ENDWHILE
                    BREAK
                ENDIF  
                IF IS_KEY_PRESSED VK_KEY_N
                    showingList = 0       
                    curColors[0] = tempColors[0]
                    curColors[1] = tempColors[1]
                    curColors[2] = tempColors[2]
                    curColors[3] = tempColors[3]
                    GOSUB ChooseColors_Set
                    WHILE IS_KEY_PRESSED VK_KEY_N
                        GOSUB ChooseColors_Interface
                    ENDWHILE
                    BREAK
                ENDIF  
                
                IF NOT IS_KEY_PRESSED VK_UP
                AND NOT IS_KEY_PRESSED VK_DOWN
                    timerb = 0
                ELSE
                    WHILE IS_KEY_PRESSED VK_UP
                        GOSUB ChooseColors_Interface
                        GOSUB ChooseColors_ShowAllColors
                        IF timerb < 200
                        ELSE
                            BREAK
                        ENDIF
                    ENDWHILE   
                    WHILE IS_KEY_PRESSED VK_DOWN
                        GOSUB ChooseColors_Interface
                        GOSUB ChooseColors_ShowAllColors
                        IF timerb < 200
                        ELSE
                            BREAK
                        ENDIF
                    ENDWHILE
                ENDIF
            ENDWHILE
        ENDIF

        /*
        IF IS_KEY_PRESSED VK_G
        
            GET_LABEL_POINTER 16bytes (15@)
            IF CLEO_CALL Keyboard 0  Pointer 15@  MaxChars 3  OnlyNumbers true  Interface 5
            
                IF SCAN_STRING bakTempOne 15@ "%i" i
                    IF i > 255
                        i = 255
                    ENDIF       
                    curOne[curIndex] = i
                    GOSUB ChooseColors_Set
                ENDIF
            ENDIF
        ENDIF
        */

        IF IS_KEY_PRESSED VK_KEY_N
            BREAK
        ENDIF

    ENDWHILE

    DO_FADE 0 150
    WAIT 150
    USE_TEXT_COMMANDS 0
    WHILE IS_KEY_PRESSED VK_KEY_Y
        WAIT 0
    ENDWHILE 
    WHILE IS_KEY_PRESSED VK_KEY_N
        WAIT 0
    ENDWHILE
    DO_FADE 1 200

    CLEO_RETURN 0




    ChooseColors_Interface:
    WAIT 0

    IF showingList = 0 //main interface
        //Footer
        //CLEO_CALL GUI_DrawBoxOutline_WithText 0 PosX 320.0 PosY 440.0 SizeX 640.0 SizeY 55.0 RGBA 0 0 0 255 OutlineSize 1.4 OutlineSides 1 0 0 0 OutlineRGBA 200 200 200 200 TextID -1     FormatID -1     Padding -1
        DRAW_RECT 320.0 440.0 640.0 55.0 0 0 0 255
        DRAW_RECT 320.0 413.0 640.0 1.4 255 255 255 255

        GOSUB FormatDescCenter               
        DISPLAY_TEXT 320.0 420.0 _TM_175    
        //GOSUB FormatDescCenter                         
        //DISPLAY_TEXT 320.0 425.0 _TM_176  
        GOSUB FormatDescCenter                            
        DISPLAY_TEXT 320.0 430.0 _TM_177          

        IF curIndex = 0 //prim
            DRAW_RECT (240.0 100.0 60.0 60.0) (255 255 255 255)
        ENDIF                               
        CLEO_CALL GetCarcolRGB 0 (curColors[0]) (r g b)
        DRAW_RECT (240.0 100.0 55.0 55.0) (r g b 255)
        GOSUB FormatMonoItemCenter
        DISPLAY_TEXT_WITH_NUMBER 240.0 100.0 NUMBER 1
        IF CLEO_CALL ThisColorIsOriginallyInstalledInSlot 0 (curColors[0] 0)
        ELSE
            CLEO_CALL DifferenciateColor 0 (r g b)(r g b)
            DRAW_SPRITE SPRITE_SHOPCART (240.0 120.0) (10.0 8.0) (r g b 255)
        ENDIF
        
        IF curIndex = 1 //sec 
            DRAW_RECT (300.0 100.0 60.0 60.0) (255 255 255 255)
        ENDIF                             
        CLEO_CALL GetCarcolRGB 0 curColors[1] (r g b)
        DRAW_RECT (300.0 100.0 55.0 55.0) (r g b 255)
        GOSUB FormatMonoItemCenter        
        DISPLAY_TEXT_WITH_NUMBER 300.0 100.0 NUMBER 2   
        IF CLEO_CALL ThisColorIsOriginallyInstalledInSlot 0 (curColors[1] 1)
        ELSE
            CLEO_CALL DifferenciateColor 0 (r g b)(r g b)
            DRAW_SPRITE SPRITE_SHOPCART (300.0 120.0) (10.0 8.0) (r g b 255)
        ENDIF   
        
        IF curIndex = 2 //3rd
            DRAW_RECT (360.0 100.0 60.0 60.0) (255 255 255 255)
        ENDIF               
        CLEO_CALL GetCarcolRGB 0 curColors[2] (r g b)  
        DRAW_RECT (360.0 100.0 55.0 55.0) (r g b 255)
        GOSUB FormatMonoItemCenter               
        DISPLAY_TEXT_WITH_NUMBER 360.0 100.0 NUMBER 3    
        IF CLEO_CALL ThisColorIsOriginallyInstalledInSlot 0 (curColors[2] 2)
        ELSE
            CLEO_CALL DifferenciateColor 0 (r g b)(r g b)
            DRAW_SPRITE SPRITE_SHOPCART (360.0 120.0) (10.0 8.0) (r g b 255)
        ENDIF    
        
        IF curIndex = 3 //4th
            DRAW_RECT (420.0 100.0 60.0 60.0) (255 255 255 255)
        ENDIF               
        CLEO_CALL GetCarcolRGB 0 curColors[3] (r g b)
        DRAW_RECT (420.0 100.0 55.0 55.0) (r g b 255)
        GOSUB FormatMonoItemCenter               
        DISPLAY_TEXT_WITH_NUMBER 420.0 100.0 NUMBER 4  
        IF CLEO_CALL ThisColorIsOriginallyInstalledInSlot 0 (curColors[3] 3)
        ELSE
            CLEO_CALL DifferenciateColor 0 (r g b)(r g b)
            DRAW_SPRITE SPRITE_SHOPCART (420.0 120.0) (10.0 8.0) (r g b 255)
        ENDIF    
    ELSE //pal  
        //Footer
        //CLEO_CALL GUI_DrawBoxOutline_WithText 20 PosX 287.0 PosY 440.0 SizeX 580.0 SizeY 55.0 RGBA 0 0 0 255 OutlineSize 1.4 OutlineSides 1 0 0 0 OutlineRGBA 200 200 200 200 TextID -1     FormatID -1     Padding -1
        DRAW_RECT 287.0 440.0 580.0 55.0 0 0 0 255
        DRAW_RECT 287.0 413.0 580.0 1.4 255 255 255 255

        GOSUB FormatDescCenter                   
        DISPLAY_TEXT 320.0 420.0 _TM_179    
        //GOSUB FormatDescCenter                        
        //DISPLAY_TEXT 320.0 425.0 _TM_180     
        GOSUB FormatDescCenter                                
        DISPLAY_TEXT 320.0 430.0 _TM_366   
    ENDIF
    
    USE_TEXT_COMMANDS 0
    RETURN


    FormatMonoItemCenter:
    SET_TEXT_COLOUR 240 240 240 255  
    SET_TEXT_FONT 2
    SET_TEXT_CENTRE ON
    SET_TEXT_CENTRE_SIZE 640.0
    SET_TEXT_EDGE 1 0 0 0 100
    SET_TEXT_SCALE 0.3 1.0
    SET_TEXT_PROPORTIONAL ON
    RETURN   

    FormatDescCenter:
    SET_TEXT_FONT 1
    SET_TEXT_SCALE 0.22 1.02
    SET_TEXT_CENTRE ON
    SET_TEXT_CENTRE_SIZE 640.0
    SET_TEXT_COLOUR  200 200 200 255
    SET_TEXT_EDGE 1 0 0 0 200
    RETURN


    ChooseColors_ShowAllColors:
    i = curColorID
    endColorId = curColorID

    i -= 24
    IF i < 0
        i *= -1
        endColorId += i
        i = 0
    ENDIF

    endColorId += 24
    IF endColorId > maxColors
        endColorId = maxColors
    ENDIF

    startColorId = i
    posY = 10.0

    DRAW_RECT (615.0 240.0 75.0 480.0) (0 0 0 150) 
    DRAW_RECT (577.0 240.0 1.6 480.0) (255 255 255 255)
    WHILE startColorId <= endColorId
        CLEO_CALL GetCarcolRGB 0 startColorId (r g b)
        IF curColorID = startColorId
            DRAW_RECT (610.0 posY) (50.0 10.0) (255 255 255 128)
        ENDIF
        DRAW_RECT (610.0 posY 30.0 10.0) (r g b 255)
        IF CLEO_CALL ThisColorIsOriginallyInstalledInSlot 0 (startColorId curIndex)
            CLEO_CALL DifferenciateColor 0 (r g b)(r g b)
            DRAW_SPRITE SPRITE_CAR (610.0 posY) (10.0 8.0) (r g b 255)
        ENDIF
        posY += 10.0
        startColorId++
    ENDWHILE
    DRAW_RECT (615.0 posY) (75.0 10.0) (0 0 0 255)
    RETURN

    ChooseColors_Set:
    CHANGE_CAR_COLOUR hVeh curColors[0] curColors[1]
    SET_EXTRA_CAR_COLOURS hVeh curColors[2] curColors[3]
    RETURN
}

{
    LVAR_INT r g b //In
    DifferenciateColor:
    IF r < 70
        r += 70
    ELSE
        r -= 70
    ENDIF
    IF g < 70
        g += 70
    ELSE
        g -= 70
    ENDIF
    IF b < 70
        b += 70
    ELSE
        b -= 70
    ENDIF
    CLEO_RETURN 0 (r g b)
}

{
    LVAR_INT colorId //In
    LVAR_INT p vehicleColors
    LVAR_INT r g b
    GetCarcolRGB:
    p = 4
    p *= colorId 
    READ_MEMORY 0x582176 4 FALSE (vehicleColors)
    p += vehicleColors //_vehicleColors  RwRGBA 80h dup
    READ_MEMORY p 1 FALSE (r)
    p++  
    READ_MEMORY p 1 FALSE (g)
    p++
    READ_MEMORY p 1 FALSE (b)
    //p++
    //READ_MEMORY p 1 FALSE (a)
    CLEO_RETURN 0 r g b
}

///////////////////////////////////////////////////////////////////////////////////////////////////


TMemory_Start:
TMemory_HOOD:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_VENT:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_SPOIL:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_SIDESKIRT:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_BULLFRNT:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_BULLREAR:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_LIGHTS:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_ROOF:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_NITRO:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_HYDRAULICS:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_STEREO:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_WHEELS: //hardcoded
DUMP 
00 00
ENDDUMP

TMemory_ALLWHEELS: //hardcoded
DUMP 
00 01
ENDDUMP

TMemory_EXHAUST:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_BUMPFRNT:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_BUMPREAR:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
FF FF FF FF
ENDDUMP

TMemory_MISC:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //16
FF FF FF FF
ENDDUMP

TMemory_Wheels_TF:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //16
FF FF FF FF
ENDDUMP

TMemory_Wheels_WAA:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //16
FF FF FF FF
ENDDUMP

TMemory_Wheels_LLC:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //16
FF FF FF FF
ENDDUMP

TMemory_Wheels_MORE:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //16
FF FF FF FF
ENDDUMP


///////////////////////////////////////////////////////////////////////////////////////////////////

InstalledParts:
DUMP
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //8
00 00  00 00  00 00  00 00  00 00  00 00  00 00  00 00 //16
FF FF FF FF
ENDDUMP

InstalledColors:
DUMP
00  00  00  00
ENDDUMP

