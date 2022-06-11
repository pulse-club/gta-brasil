// Car Dealership by Junior_Djjr - MixMods.com.br
// You need: https://forum.mixmods.com.br/f16-utilidades/t179-gta3script-while-true-return_true-e-return_false
SCRIPT_START
{
    LVAR_INT i scplayer cars[5] bikes[5] p hMenu iItem iLastItem hVeh iPrice hChar bVendorNotValid pExt_SetCarSeed pExt_SetVehicleKMs
    LVAR_INT iModel parkedVehId seed color weekDay unit
    LVAR_FLOAT x y f oldFactor

    WAIT 1000

    GET_PLAYER_CHAR 0 scplayer
    
    //SET_CHAR_COORDINATES scplayer 2127.2144 -1124.3822 25.0

    IF NOT READ_INT_FROM_INI_FILE "CLEO/Car Dealership.ini" "Settings" "Unit" (unit)
        timera = 0
        WHILE timera < 5000
            WAIT 0
            PRINT_STRING_NOW "~r~Error: Fail to read 'Car Dealership.ini'" 1000
        ENDWHILE
        TERMINATE_THIS_CUSTOM_SCRIPT
    ENDIF

    WHILE TRUE
        WAIT 0
        
        IF LOCATE_CHAR_ANY_MEANS_3D scplayer 2119.8364 -1124.3458 30.0 200.0 200.0 120.0 FALSE

            IF LOAD_DYNAMIC_LIBRARY "VehFuncs.asi" (i)
                IF NOT GET_DYNAMIC_LIBRARY_PROCEDURE "Ext_SetCarSeed" i (pExt_SetCarSeed)
                    pExt_SetCarSeed = 0
                ENDIF
                IF NOT GET_DYNAMIC_LIBRARY_PROCEDURE "Ext_SetVehicleKMs" i (pExt_SetVehicleKMs)
                    pExt_SetVehicleKMs = 0
                ENDIF
                FREE_DYNAMIC_LIBRARY i
            ENDIF

            GET_CURRENT_DAY_OF_WEEK (weekDay)

            CLEAR_AREA_OF_CARS 2139.6006 -1154.752 30.0 2115.9377 -1119.6849 20.0
            
            REPEAT 10 parkedVehId
                GOSUB CreateParkedVehicle
            ENDREPEAT

            // Reset random seed
            GET_GAME_TIMER i
            CALL_FUNCTION 0x821B11 2 2 seed i //srand

            REQUEST_MODEL OMOST
            WHILE NOT HAS_MODEL_LOADED OMOST
                WAIT 0
            ENDWHILE
            CREATE_CHAR PEDTYPE_MISSION8 OMOST 2119.0 -1121.6185 24.5 (hChar)
            SET_CHAR_HEADING hChar -90.0
            SET_CHAR_STAY_IN_SAME_PLACE hChar ON
            MARK_MODEL_AS_NO_LONGER_NEEDED OMOST

            bVendorNotValid = FALSE

            main_loop:
            WHILE TRUE
                WAIT 0

                IF TEST_CHEAT CDEALERNEXT
                    SET_TIME_ONE_DAY_FORWARD
                    GET_CURRENT_DAY_OF_WEEK weekDay
                    PRINT_FORMATTED_NOW "Day %d" 2000 weekDay
                    BREAK
                ENDIF

                IF TEST_CHEAT CDEALERRELOAD
                    PRINT_STRING_NOW "'Car Dealership.ini' reloaded." 2000
                    BREAK
                ENDIF

                IF NOT LOCATE_CHAR_ANY_MEANS_3D scplayer 2119.8364 -1124.3458 30.0 230.0 230.0 150.0 FALSE
                    BREAK
                ENDIF

                REPEAT 10 i // 10 will make it overflow to bikes[i] too
                    IF DOES_VEHICLE_EXIST cars[i]
                        IF NOT IS_CAR_HEALTH_GREATER cars[i] 500
                        OR HAS_CAR_BEEN_DAMAGED_BY_CHAR cars[i] scplayer
                        OR IS_CHAR_SITTING_IN_CAR scplayer cars[i]
                            IF IS_CHAR_SITTING_IN_CAR scplayer cars[i]
                                IF LOCATE_CHAR_ANY_MEANS_3D scplayer 2119.8364 -1124.3458 30.0 25.0 25.0 20.0 FALSE
                                    ALTER_WANTED_LEVEL_NO_DROP 0 2
                                ENDIF
                            ENDIF
                            IF NOT bVendorNotValid = TRUE
                                MARK_CHAR_AS_NO_LONGER_NEEDED hChar
                                TASK_FLEE_POINT hChar 2118.0 -1121.6185 24.5 50.0 5000
                                bVendorNotValid = TRUE
                            ENDIF
                        ENDIF
                    ENDIF
                ENDREPEAT

                IF bVendorNotValid = TRUE
                    CONTINUE
                ENDIF

                IF GOSUB NotValidToSell
                    CONTINUE
                ENDIF

                IF LOCATE_STOPPED_CHAR_ON_FOOT_3D scplayer 2119.8643 -1121.6185 25.0 1.5 1.5 1.0 TRUE

                    SET_PLAYER_CONTROL 0 OFF
                    TASK_LOOK_AT_CHAR scplayer hChar 4000
                    TASK_TURN_CHAR_TO_FACE_CHAR scplayer hChar
                    TASK_LOOK_AT_CHAR hChar scplayer 4000
                    TASK_TURN_CHAR_TO_FACE_CHAR hChar scplayer

                    PRINT_NOW CNSS02A 3000 1
                    WAIT 2000

                    iItem = 0
                    iLastItem = -1

                    create_list_menu:
                    //08D4=9,create_menu %1g% position %2d% %3d% width %4d% columns %5h% interactive %6h% background %7h% alignment %8h% store_to %9d%
                    CREATE_MENU CNSS01C (20.0 150.0) (125.0) 2 (ON ON 1) (hMenu)

                    //08DB=15,set_menu_column %1d% col %2h% title_to %3g% items_to %4g% %5g% %6g% %7g% %8g% %9g%   %10g% %11g% %12g% %13g% %14g% %15g%
                    SET_MENU_COLUMN hMenu 0 CNSS01B (DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY) // Modelo
                    IF unit = 1
                        SET_MENU_COLUMN hMenu 1 CNSS011 (CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I DUMMY DUMMY) // KMs
                    ELSE
                        SET_MENU_COLUMN hMenu 1 CNSS012 (CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I CNSS01I DUMMY DUMMY) // Milhas
                    ENDIF
                    SET_MENU_COLUMN_ORIENTATION hMenu 0 1
                    SET_MENU_COLUMN_WIDTH hMenu 1 50
                    SET_ACTIVE_MENU_ITEM hMenu 0
                    SET_ACTIVE_MENU_ITEM hMenu iItem

                    CLEO_CALL SetMenuList 0 (hMenu cars[0] cars[1] cars[2] cars[3] cars[4] bikes[0] bikes[1] bikes[2] bikes[3] bikes[4] unit)
                    CLEO_CALL SetStoredNames 0 (hMenu)

                    WHILE TRUE
                        IF GOSUB ProcessAndCheck
                            BREAK
                        ENDIF

                        GET_MENU_ITEM_SELECTED hMenu (iItem)

                        // Item changed
                        IF NOT iItem = iLastItem
                            i = iItem
                            IF iItem < 5 // is cars
                                hVeh = cars[i]
                                IF hVeh > -1
                                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS hVeh 3.0 5.0 0.7 (x y f)
                                ENDIF
                            ELSE
                                bike_selected:
                                i -= 5
                                hVeh = bikes[i]
                                IF hVeh > -1
                                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS hVeh 2.0 3.0 0.7 (x y f)
                                ENDIF
                            ENDIF
                            IF hVeh > -1
                                SET_FIXED_CAMERA_POSITION x y f 0.0 0.0 0.0
                                GET_CAR_COORDINATES hVeh x y f
                                POINT_CAMERA_AT_POINT x y f 1
                            ENDIF
                            iLastItem = iItem
                        ENDIF

                        IF IS_BUTTON_PRESSED PAD1 TRIANGLE
                            
                            CLEAR_PRINTS
                            DELETE_MENU hMenu
                            DO_FADE 300 FADE_OUT
                            WAIT 300
                            SET_CHAR_COORDINATES scplayer 2125.0 -1124.0 -100.0
                            SET_CHAR_HEADING scplayer 0.0
                            RESTORE_CAMERA_JUMPCUT
                            WAIT 200
                            DO_FADE 800 FADE_IN
                            
                            PRINT_NOW CNSS02B 3000 1
                            BREAK
                        ENDIF

                        IF NOT hVeh > -1
                            CONTINUE
                        ENDIF

                        IF IS_BUTTON_PRESSED PAD1 CROSS
                            DELETE_MENU hMenu

                            CREATE_MENU CNSS01D (20.0 150.0) (125.0) 2 (ON ON 1) (hMenu)
                            SET_MENU_COLUMN hMenu 0 CNSS01B (DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY)
                            SET_MENU_COLUMN hMenu 1 CNSS01A (DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY)
                            CLEO_CALL SetMenuThisVeh 0 (hMenu hVeh iItem)
                            
                            WHILE IS_BUTTON_PRESSED PAD1 CROSS
                                IF GOSUB ProcessAndCheck
                                    BREAK
                                ENDIF
                            ENDWHILE

                            WHILE TRUE
                                IF GOSUB ProcessAndCheck
                                    BREAK
                                ENDIF

                                IF IS_BUTTON_PRESSED PAD1 CROSS
                                    GET_CAR_MODEL hVeh (iModel)
                                    GET_CAR_MODEL_VALUE iModel (iPrice)

                                    CLEO_CALL GetCalculatedPrice 0 (iPrice iItem)(iPrice)
                                    
                                    STORE_SCORE 0 i
                                    IF i > iPrice
                                        iPrice *= -1
                                        ADD_SCORE 0 iPrice
                                        CLEAR_PRINTS
                                        DELETE_MENU hMenu
                                        DO_FADE 750 FADE_OUT
                                        SET_CHAR_PROOFS scplayer 1 1 1 1 1
                                        WAIT 800
                                        IF GET_RANDOM_CAR_IN_SPHERE_NO_SAVE_RECURSIVE 2128.2 -1124.5 25.0 5.0 FALSE FALSE (i)
                                            CLEO_CALL SafeThisCar 0 (i)
                                            WHILE GET_RANDOM_CAR_IN_SPHERE_NO_SAVE_RECURSIVE 2128.2 -1124.5 25.0 5.0 TRUE FALSE (i)
                                                CLEO_CALL SafeThisCar 0 (i)
                                            ENDWHILE
                                        ENDIF
                                        IF iItem < 5 // is cars
                                            cars[iItem] = -1
                                            LOCK_CAR_DOORS hVeh CARLOCK_NONE
                                            SET_CAR_COORDINATES hVeh 2128.2 -1124.5 -100.0
                                            SET_CAR_HEADING hVeh 0.0
                                            SET_CHAR_COORDINATES scplayer 2125.3 -1125.0 -100.0
                                            SET_CHAR_HEADING scplayer -90.0
                                            OPEN_CAR_DOOR hVeh 2
                                            PRINT_HELP CNSS01F
                                        ELSE
                                            iItem -= 5
                                            bikes[iItem] = -1
                                            SET_CAR_COORDINATES hVeh 2128.0 -1124.5 -100.0
                                            SET_CAR_HEADING hVeh 0.0
                                            SET_CHAR_COORDINATES scplayer 2126.0 -1124.5 -100.0
                                            SET_CHAR_HEADING scplayer -90.0
                                            PRINT_HELP CNSS01G
                                        ENDIF
                                        GET_VEHICLE_POINTER hVeh (p)
                                        p += 0x42F
                                        READ_MEMORY p 1 FALSE (i)
                                        SET_LOCAL_VAR_BIT_CONST i 7
                                        WRITE_MEMORY p 1 i FALSE
                                        GOSUB ResetPlayer
                                        WAIT 500
                                        SET_CHAR_PROOFS scplayer 0 0 0 0 0
                                        DO_FADE 1500 FADE_IN
                                        MARK_CAR_AS_NO_LONGER_NEEDED hVeh
                                        GOTO main_loop
                                    ELSE
                                        i -= iPrice
                                        i *= -1
                                        PRINT_WITH_NUMBER_NOW CNSS01E i 3000 1
                                    ENDIF
                                ENDIF

                                IF IS_BUTTON_PRESSED PAD1 TRIANGLE
                                    WHILE IS_BUTTON_PRESSED PAD1 TRIANGLE
                                        IF GOSUB ProcessAndCheck
                                            BREAK
                                        ENDIF
                                    ENDWHILE
                                    DELETE_MENU hMenu
                                    GOTO create_list_menu
                                ENDIF

                            ENDWHILE

                        ENDIF

                    ENDWHILE

                    DELETE_MENU hMenu
                    GOSUB ResetPlayer

                ENDIF

            ENDWHILE

            REPEAT 10 parkedVehId
                GOSUB DeleteParkedVehicle
            ENDREPEAT

            GOSUB DeleteVendor

        ENDIF

    ENDWHILE


    ProcessAndCheck:
    WAIT 0
    SET_PLAYER_CONTROL 0 OFF
    DISPLAY_RADAR OFF
    IF HAS_CHAR_BEEN_ARRESTED scplayer
    OR IS_CHAR_DEAD scplayer
        RETURN_TRUE
        RETURN
    ENDIF
    IF NOT DOES_CHAR_EXIST hChar
    OR IS_CHAR_DEAD hChar
        PRINT_NOW CNSS02B 4000 1
        RETURN_TRUE
        RETURN
    ENDIF
    RETURN_FALSE
    RETURN

    ResetPlayer:
    SET_PLAYER_CONTROL 0 ON
    DISPLAY_RADAR ON
    RESTORE_CAMERA
    RETURN

    NotValidToSell:
    IF NOT DOES_CHAR_EXIST hChar
    OR IS_CHAR_DEAD hChar
        bVendorNotValid = TRUE
        RETURN_TRUE
        RETURN
    ENDIF
    IF NOT IS_CHAR_HEALTH_GREATER hChar 95
        MARK_CHAR_AS_NO_LONGER_NEEDED hChar
        TASK_FLEE_POINT hChar 2118.0 -1121.6185 24.5 50.0 5000
        bVendorNotValid = TRUE
        RETURN_TRUE
        RETURN
    ENDIF
    IF IS_WANTED_LEVEL_GREATER 0 0
        RETURN_TRUE
        RETURN
    ENDIF
    RETURN_FALSE
    RETURN


    DeleteParkedVehicle:
    IF cars[parkedVehId] > -1
        IF DOES_VEHICLE_EXIST cars[parkedVehId]
            IF LOCATE_CAR_3D cars[parkedVehId] 2119.8364 -1124.3458 50.0 50.0 50.0 20.0 FALSE
                DELETE_CAR cars[parkedVehId]
            ELSE
                MARK_CAR_AS_NO_LONGER_NEEDED cars[parkedVehId]
            ENDIF
        ENDIF
    ENDIF
    RETURN

    DeleteVendor:
    IF DOES_CHAR_EXIST hChar
        IF LOCATE_CHAR_ANY_MEANS_3D hChar 2119.8364 -1124.3458 50.0 50.0 50.0 20.0 FALSE
            DELETE_CHAR hChar
        ELSE
            MARK_CHAR_AS_NO_LONGER_NEEDED hChar
        ENDIF
    ENDIF
    RETURN

    CreateParkedVehicle:
    GOSUB GetCoordForParkedVehicle
    GOSUB GetDataForParkedVehicle
    REQUEST_MODEL iModel
    WHILE NOT HAS_MODEL_LOADED iModel
        WAIT 0
    ENDWHILE

    GOSUB GetSeedForParkedVehicle // It also sets random seed

    GENERATE_RANDOM_INT_IN_RANGE 1 6 (i)
    GENERATE_RANDOM_INT_IN_RANGE 1 6 (p)
    SET_CAR_MODEL_COMPONENTS iModel i p

    CREATE_CAR iModel x y -100.0 (cars[parkedVehId])
    MARK_MODEL_AS_NO_LONGER_NEEDED iModel
    SET_CAR_HEADING cars[parkedVehId] (f)

    IF oldFactor > 0.2
    AND parkedVehId < 5
        GENERATE_RANDOM_FLOAT_IN_RANGE 0.0 1.0 (f)
        WHILE f < oldFactor
            GENERATE_RANDOM_INT_IN_RANGE 0 11 (i) //11?
            IF i > 5
                DAMAGE_CAR_DOOR cars[parkedVehId] i
            ELSE    
                i -= 5
                DAMAGE_CAR_PANEL cars[parkedVehId] i
            ENDIF
            f += 0.1
        ENDWHILE
    ENDIF

    f = 300.0
    f *= oldFactor
    y = 1000.0
    y -= f
    IF y > 1000.0
        y = 1000.0
    ELSE
        IF y < 300.0
            y = 300.0
        ENDIF
    ENDIF

    i =# y
    
    SET_CAR_HEALTH cars[parkedVehId] i

    GET_VEHICLE_POINTER cars[parkedVehId] (p)

    IF pExt_SetCarSeed > 0
        CALL_FUNCTION pExt_SetCarSeed 2 2 (seed p)  // Ext_SetCarSeed(CVehicle * vehicle, int seed)
    ENDIF


    // Calculate KMs
    f = 2000000.0 * oldFactor // '200.000-0'.0
    timera =# f
    f = 2500000.0 * oldFactor // '250.000-0'.0
    timerb =# f
    GENERATE_RANDOM_INT_IN_RANGE timera timerb (timerb)

    // Set KMs to VehFuncs
    IF oldFactor > 0.0
    AND pExt_SetVehicleKMs > 0
        CALL_FUNCTION pExt_SetVehicleKMs 2 2 (timerb p)  // Ext_SetVehicleKMs(CVehicle * vehicle, int kms)
    ENDIF

    // Store KMs
    GET_LABEL_POINTER AndYetINeedMoreVarsForKMsAndIDontWantToUseExtendedVarsJustForThis p
    i = parkedVehId * 4
    p += i
    WRITE_MEMORY p 4 timerb FALSE
    
    // Store old factor
    GET_LABEL_POINTER INeedMoreVarsForOldFactorAndIDontWantToUseExtendedVarsJustForThis p
    i = parkedVehId * 4
    p += i
    WRITE_MEMORY p 4 oldFactor FALSE

    // Set color
    CALL_FUNCTION_RETURN 0x00403DA0 1 1 (iModel)(p) // GetModelInfo
    i = p + 0x2D0 // m_nNumColorVariations
    READ_MEMORY i 1 FALSE (timera)
    IF color > timera
        color = timera
    ENDIF
    i = color + 0x2B0 // m_anPrimaryColors[i]
    i += p
    READ_MEMORY i 1 FALSE (timera)
    i += 0x8
    READ_MEMORY i 1 FALSE (timerb)
    CHANGE_CAR_COLOUR cars[parkedVehId] timera timerb
    i += 0x8
    READ_MEMORY i 1 FALSE (timera)
    i += 0x8
    READ_MEMORY i 1 FALSE (timerb)
    SET_EXTRA_CAR_COLOURS cars[parkedVehId] timera timerb

    // Lock it
    IF parkedVehId < 4
        LOCK_CAR_DOORS cars[parkedVehId] CARLOCK_LOCKED
    ENDIF

    RETURN

    GetCoordForParkedVehicle:
    SWITCH (parkedVehId)
        CASE 0
            x = 2119.8364 
            y = -1124.3458
            BREAK
        CASE 1
            x = 2119.6965
            y = -1128.6963
            BREAK
        CASE 2
            x = 2119.5701 
            y = -1132.9762
            BREAK
        CASE 3
            x = 2119.3401 
            y = -1137.3442
            BREAK
        CASE 4
            x = 2119.1531 
            y = -1141.9941
            BREAK
        CASE 5
            x = 2137.5281
            y = -1126.756
            BREAK
        CASE 6
            x = 2137.605
            y = -1128.7001
            BREAK
        CASE 7
            x = 2137.4885
            y = -1131.0684
            BREAK
        CASE 8
            x = 2137.5959
            y = -1133.4594
            BREAK
        CASE 9
            x = 2137.7561
            y = -1135.7251
            BREAK
    ENDSWITCH
    IF parkedVehId <= 4
        f = 270.0
    ELSE
        f = 90.0
    ENDIF
    RETURN

    GetDataForParkedVehicle:
    GET_LABEL_POINTER Buffer (i)
    STRING_FORMAT i "Day%d" weekDay
    
    GET_LABEL_POINTER BufferB (p)

    parkedVehId++

    STRING_FORMAT p "Slot%dModel" parkedVehId
    READ_INT_FROM_INI_FILE "CLEO/Car Dealership.ini" $i $p (iModel)

    STRING_FORMAT p "Slot%dColor" parkedVehId
    READ_INT_FROM_INI_FILE "CLEO/Car Dealership.ini" $i $p (color)

    STRING_FORMAT p "Slot%dOld" parkedVehId
    READ_FLOAT_FROM_INI_FILE "CLEO/Car Dealership.ini" $i $p (oldFactor)

    color--
    IF color < 1
        color = 1
    ENDIF

    parkedVehId--
    RETURN

    GetSeedForParkedVehicle:
    GET_LABEL_POINTER Buffer (i)
    STRING_FORMAT i "Day%d" weekDay
    
    GET_LABEL_POINTER BufferB (p)

    parkedVehId++
    
    STRING_FORMAT p "Slot%dSeed" parkedVehId
    READ_INT_FROM_INI_FILE "CLEO/Car Dealership.ini" $i $p (seed)

    parkedVehId--

    GET_CURRENT_DATE (timera timerb)

    seed += timera
    seed += timerb

    i = seed * timera
    i /= timerb
    i += iModel

    CALL_FUNCTION 0x821B11 2 2 seed i //srand
    RETURN

}
SCRIPT_END

{
    LVAR_INT slot // In
    LVAR_INT i p 
    LVAR_FLOAT oldFactor
    GetOldFactor:
    GET_LABEL_POINTER INeedMoreVarsForOldFactorAndIDontWantToUseExtendedVarsJustForThis p
    i = slot * 4
    p += i
    READ_MEMORY p 4 FALSE oldFactor
    CLEO_RETURN 0 oldFactor
}

{
    LVAR_INT oldPrice slot // In
    LVAR_INT newPrice i p
    LVAR_FLOAT oldFactor f g
    GetCalculatedPrice:
    CLEO_CALL GetOldFactor 0 (slot)(oldFactor)

    f =# oldPrice
    f *= 0.4

    f *= oldFactor
    g =# oldPrice
    g -= f
    newPrice =# g
    CLEO_RETURN 0 (newPrice)
}

{
    LVAR_INT slot // In
    LVAR_INT i p iKMs
    GetCalculatedKMs:
    GET_LABEL_POINTER AndYetINeedMoreVarsForKMsAndIDontWantToUseExtendedVarsJustForThis p
    i = slot * 4
    p += i
    READ_MEMORY p 4 FALSE iKMs
    CLEO_RETURN 0 (iKMs)
}

{
    LVAR_INT hVeh // In
    LVAR_INT hVeh2
    LVAR_FLOAT x y z

    SafeThisCar:
    x = 2148.0
    y = -1134.0
    z = 25.0
    WHILE GET_RANDOM_CAR_IN_SPHERE_NO_SAVE_RECURSIVE x y z 2.0 FALSE FALSE (hVeh2)
        y -= 4.5
    ENDWHILE
    SET_CAR_COORDINATES hVeh x y -100.0
    SET_CAR_HEADING hVeh 90.0
    CLOSE_ALL_CAR_DOORS hVeh
    CLEO_RETURN 0 
}

{
    LVAR_INT hMenu hVeh iSlot // In
    LVAR_INT iModel iPrice i
    LVAR_TEXT_LABEL tName

    SetMenuThisVeh:
    GET_CAR_MODEL hVeh (iModel)
    GET_NAME_OF_VEHICLE_MODEL iModel tName
    SET_MENU_COLUMN hMenu 0 CNSS01B ($tName DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY DUMMY)
    GET_CAR_MODEL_VALUE iModel (iPrice)
    CLEO_CALL GetCalculatedPrice 0 (iPrice iSlot)(iPrice)
    SET_MENU_ITEM_WITH_NUMBER hMenu 1 i CNSS010 iPrice
    CLEO_RETURN 0
}

{
    LVAR_INT hMenu vehs[10] unit // In
    LVAR_INT i j iModel iPrice iKMs p
    LVAR_FLOAT oldFactor fKMs
    LVAR_TEXT_LABEL tName

    SetMenuList:
    REPEAT 10 i
        IF DOES_VEHICLE_EXIST vehs[i]
            GET_CAR_MODEL vehs[i] (iModel)
            /*GET_CAR_MODEL_VALUE iModel (iPrice)
            CLEO_CALL GetCalculatedPrice 0 (iPrice i)(iPrice)*/
            CLEO_CALL GetCalculatedKMs 0 (i)(iKMs)
            iKMs /= 10
            GET_NAME_OF_VEHICLE_MODEL iModel tName
            GET_LABEL_POINTER Names (p)
            j = i * 8
            j += p
            STRING_FORMAT j "%s" $tName
            IF NOT unit = 1
                fKMs =# iKMs
                fKMs *= 0.621371
                iKMs =# fKMs
            ENDIF
            SET_MENU_ITEM_WITH_NUMBER hMenu 1 i NUMBER iKMs
        ELSE
            GET_LABEL_POINTER Names (p)
            j = i * 8
            j += p
            STRING_FORMAT j "CNSS01I"
        ENDIF
    ENDREPEAT
    CLEO_RETURN 0
}

{
    LVAR_INT hMenu // In
    LVAR_INT i j p
    LVAR_TEXT_LABEL tNames[10]

    SetStoredNames:
    REPEAT 10 i
        GET_LABEL_POINTER Names (p)
        j = i * 8
        j += p
        STRING_FORMAT tNames[i] "%s" j
    ENDREPEAT
    SET_MENU_COLUMN hMenu 0 CNSS01B ($tNames[0] $tNames[1] $tNames[2] $tNames[3] $tNames[4] $tNames[5] $tNames[6] $tNames[7] $tNames[8] $tNames[9] DUMMY DUMMY)
    CLEO_RETURN 0
}

Buffer:
DUMP
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
ENDDUMP

BufferB:
DUMP
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
ENDDUMP

Names:
DUMP
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
ENDDUMP 

INeedMoreVarsForOldFactorAndIDontWantToUseExtendedVarsJustForThis:
DUMP
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
ENDDUMP

AndYetINeedMoreVarsForKMsAndIDontWantToUseExtendedVarsJustForThis:
DUMP
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
ENDDUMP