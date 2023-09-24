import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.LinkedList
import java.util.PriorityQueue
import java.util.Queue
import kotlin.random.Random

class `Alphametics Solver` {
    //https://www.codewars.com/kata/5b5fe164b88263ad3d00250b

    //Up to 10 letters
    fun combinations (len:Int, from:List<Int>):List<IntArray>{
        val results = mutableListOf<IntArray>()
        from.forEach { component ->
            if(len > 1)
                combinations(len - 1, from.mapNotNull { v -> if (v != component) v else null })
                    .forEach {
                        results.add(it + component)
                    }
            else
                results.add(intArrayOf(component))
        }
        return results
    }
    fun getSumVariations(sumChars: List<Char>, sum:Int, addition: Int, from: List<Int>):List<Pair<Map<Char,Int>,Int>>{
        val sumCharsMap = sumChars.groupBy { it }.map { it.key to it.value.size }

        val combinations = combinations(sumCharsMap.size,from)

        val result:MutableList<Pair<Map<Char,Int>,Int>> = mutableListOf()

        combinations.forEach {combination->
            val tmp = sumCharsMap.zip(combination.toList()).map { it.first.first to intArrayOf(it.first.second,it.second) }.toMap()
            val sumOf  = tmp.values.sumBy { it[0]*it[1] } + addition
            if(sumOf % 10 == sum)
                result.add(tmp.map { (it.key to it.value[1])}.toMap() to sumOf/10)
        }
        return result
    }

    fun alphametics_rec(puzzle: String, digits:List<Int>, results:MutableList<String>, rIndex:Int = 1, addition: Int = 0){
        if(results.isNotEmpty())
            return
        val words = puzzle.split("\\s*[+=]\\s*".toRegex())
        if (words.any { it.startsWith('0') })
            return
        if(rIndex > words.last().length || digits.isEmpty() || !puzzle.any { it.isLetter() }) {
            if(words.sumBy { if(it === words.last()) 0 else it.toInt() } == words.last().toInt())
                results.add(puzzle)
            return
        }

        val eqChar = puzzle.let { it[it.length - rIndex] }
        val _digits = if(eqChar.isLetter()) digits else listOf("$eqChar".toInt())
        for (digit in _digits){
            val tmpDigits = digits.toMutableList().apply { this.remove(digit) }

            val _puzzle = puzzle.replace(eqChar,"$digit"[0])
            val sumChars = _puzzle.split("\\s*[+=]\\s*".toRegex())
                .let{words->words.mapNotNull { if(words.last() === it || it.length < rIndex) null else it[it.length - rIndex] }}

            val replacementChars = sumChars.mapNotNull { if(it.isLetter()) it else null }
            val additions = sumChars.sumBy { if(it.isDigit()) "$it".toInt() else 0 } + addition
            if(replacementChars.isEmpty() && additions % 10 == digit)
                alphametics_rec(_puzzle, tmpDigits, results, rIndex + 1, additions / 10)
            else {
                val sumVariations = getSumVariations(replacementChars,digit,additions,tmpDigits)
                sumVariations.forEach { values ->
                    val outDigits = tmpDigits.toMutableList().apply { values.first.forEach { this.remove(it.value) } }
                    var tmpPuzzle = _puzzle
                    values.first.forEach { k,v-> tmpPuzzle = tmpPuzzle.replace(k,"$v"[0]) }
                    alphametics_rec(tmpPuzzle, outDigits, results,rIndex + 1, values.second)
                }
            }
        }
    }

    fun alphametics(puzzle: String): String {
        val results = mutableListOf<String>()
        alphametics_rec(puzzle, (0..9).toList(),results)
        return results[0]
    }

    data class Puzzle(val equasion: String, val availableDigits:List<Int> = (0..9).toList(), val rIndex:Int = 1, val sumAddition:Int = 0){}


    //WORKS A LOT WORSE THAN RECURSION
    fun alphametics_Queue(p: String):String{
        val queue =  PriorityQueue<Puzzle>(compareBy { it.rIndex })
        queue.add(Puzzle(p))
        while (queue.isNotEmpty()) {

            val puzzle = queue.poll()!!

            val words = puzzle.equasion.split("\\s*[+=]\\s*".toRegex())
            if (words.any { it.startsWith('0') })
                continue
            if (puzzle.rIndex > words.last().length) {
                if (words.sumBy { if (it === words.last()) 0 else it.toInt() } == words.last().toInt())
                    return puzzle.equasion
                else continue
            }

            val eqChar = puzzle.equasion.let { it[it.length - puzzle.rIndex] }
            val _digits = if (eqChar.isLetter()) puzzle.availableDigits else listOf("$eqChar".toInt())
            for (digit in _digits) {
                val tmpDigits = puzzle.availableDigits.toMutableList().apply { this.remove(digit) }

                val _puzzle = puzzle.equasion.replace(eqChar, "$digit"[0])
                val sumChars = _puzzle.split("\\s*[+=]\\s*".toRegex())
                    .let { words -> words.mapNotNull { if (words.last() === it || it.length < puzzle.rIndex) null else it[it.length - puzzle.rIndex] } }

                val replacementChars = sumChars.mapNotNull { if (it.isLetter()) it else null }
                val additions = sumChars.sumBy { if (it.isDigit()) "$it".toInt() else 0 } + puzzle.sumAddition
                if (replacementChars.isEmpty() && additions % 10 == digit)
                    queue.add(Puzzle(_puzzle,tmpDigits,puzzle.rIndex + 1,additions/10))
                else {
                    val sumVariations = getSumVariations(replacementChars, digit, additions, tmpDigits)
                    sumVariations.forEach { values ->
                        val outDigits =
                            tmpDigits.toMutableList().apply { values.first.forEach { this.remove(it.value) } }
                        var tmpPuzzle = _puzzle
                        values.first.forEach { k, v -> tmpPuzzle = tmpPuzzle.replace(k, "$v"[0]) }
                        queue.add(Puzzle(tmpPuzzle,outDigits,puzzle.rIndex + 1, values.second))
                    }
                }
            }
        }
        return ""
    }
    private fun runTest(puzzle:String,sol:String) = assertEquals(sol,alphametics(puzzle))

    @Test
    fun testVariations2(){
        val _testVariations:(expectedItems:Int,items:List<Pair<Map<Char,Int>,Int>>)->Unit = {expectedItems, items ->
            println( items.map { "${it.first.map { "${it}" }},(${it.second})" })
            assertEquals(expectedItems,items.size)
        }
//        _testVariations(6,getSumVariations("ABC".toList(),6,0, listOf(1,2,3)))
//        _testVariations(0,getSumVariations("ABC".toList(),6,0, listOf(1,2)))
//        _testVariations(1,getSumVariations("AAC".toList(),5,0, listOf(1,2)))
//        _testVariations(2,getSumVariations("AABB".toList(),7,1, listOf(1,2)))
//        _testVariations(12,getSumVariations("AABCD".toList(),7,0, listOf(0,1,2,3,4,5)))

        _testVariations(4,getSumVariations("AB".toList(),1,0, listOf(9,2,1,0)))
    }


    @Test
    fun testCombinations(){
        val _testCombinations:(expectedItems:Int,items:List<IntArray>)->Unit = {expectedItems, items ->
            assertEquals(expectedItems,items.size)
            println( items.map { "${it.map { "$it" }}" })
        }
        _testCombinations(2,combinations(2, listOf(0,1)))
        _testCombinations(6,combinations(2, listOf(0,1,2)))
        _testCombinations(20,combinations(2, listOf(0,1,2,3,4)))
        _testCombinations(60,combinations(3, listOf(0,1,2,3,4)))
    }

    @Test
    fun `Example Tests`() {
        runTest("BAT + FOOD = GUANO", "865 + 9772 = 10637")
//        runTest("SEND = SEND", "3210 = 3210")
//        runTest("SEND + MORE = MONEY","9567 + 1085 = 10652")
//        runTest("ZEROES + ONES = BINARY","698392 + 3192 = 701584")
//        runTest("COUPLE + COUPLE = QUARTET","653924 + 653924 = 1307848")
//        runTest("DO + YOU + FEEL = LUCKY","57 + 870 + 9441 = 10368")
//        runTest("ELEVEN + NINE + FIVE + FIVE = THIRTY","797275 + 5057 + 4027 + 4027 = 810386")
    }
    fun puzzleGenerator(argumentsCount:Int, sum:Int):Pair<String,String>{
        val argumentsSet:MutableSet<Int> = mutableSetOf()
        var remainder = sum
        for (i in (1 .. argumentsCount-1)){
            val tmp = Random.nextInt(9,9 + sum/argumentsCount)
            argumentsSet.add(tmp)
            remainder -= tmp
        }
        argumentsSet.add(remainder)
        val digitiEquasion = "${argumentsSet.joinToString(" + ")} = $sum"
        var letterEquasion = digitiEquasion
        val mappedSet:MutableSet<Char> = mutableSetOf()
        var letter:Char? = null
        do {
            letter = letterEquasion.find { it.isDigit() }
            letter?.let {
                var replacement:Char = 'a'
                while (true) {
                    replacement = Random.nextInt(65, 91).toChar()
                    if (!mappedSet.contains(replacement)) {
                        mappedSet.add(replacement)
                        break
                    }
                }
                letterEquasion = letterEquasion.replace(letter,replacement)
            }
        }while(letter != null)
        return letterEquasion to digitiEquasion

    }
    @Test
    fun testByPuzzleGenerator(){
        val pazzles:MutableList<Pair<String,String>> = mutableListOf()
        val minDigitsOfSum = 3
        val maxDigitsOfSum = 7
        val numberOfPuzzles = 150
        for(i in (minDigitsOfSum until  maxDigitsOfSum))
            for(j in (0 .. numberOfPuzzles/(maxDigitsOfSum - minDigitsOfSum))){
                val minBound = Math.pow(10.0,2.0).toInt()
                val maxBound = Math.pow(10.0,i.toDouble()).toInt() - minBound
                pazzles.add(puzzleGenerator(i,Random.nextInt(minBound, maxBound)))
            }

        val startTime = System.currentTimeMillis()

        println("Executing: ${pazzles.size} tests")
        pazzles.forEach {
//            println("Running test: $it")
//            println("Result: ${alphametics(it.first)}")
            alphametics(it.first)
        }
        println("Running for ${System.currentTimeMillis() - startTime} msec")

    }
    @Test
    fun `Submission test`(){
        val startTime = System.currentTimeMillis()
        runTest("WS + OW = XOM","93 + 49 = 142")
        runTest("KS + IMB = MKG","17 + 893 = 910")
        runTest("WS + OW = XOM","93 + 49 = 142")
        runTest("KS + IMB = MKG","17 + 893 = 910")
        runTest("XM + DDL = MVD","82 + 119 = 201")
        runTest("QZ + QE = RZ","21 + 20 = 41")
        runTest("VK + MVK = MRM","31 + 231 = 262")
        runTest("PP + DA = TAP","22 + 80 = 102")
        runTest("ZZ + NS = AZR","88 + 92 = 180")
        runTest("VK + HH = KFP","81 + 22 = 103")
        runTest("EH + OCJ = HBR","79 + 841 = 920")
        runTest("WX + OZY + ZZXM = ZTKT","50 + 219 + 1104 = 1373")
        runTest("TR + GTNR + UKUXC = KDUGN","98 + 3908 + 67624 = 71630")
        runTest("WX + OZY + ZZXM = ZTKT","50 + 219 + 1104 = 1373")
        runTest("TR + GTNR + UKUXC = KDUGN","98 + 3908 + 67624 = 71630")
        runTest("QHQR + LRCZ + XCHY = XQCHR","5950 + 8047 + 1493 = 15490")
        runTest("MXXH + XOX + LMM = MJPM","1228 + 262 + 411 = 1901")
        runTest("ZL + ZSL + IAG = PLLO","94 + 984 + 362 = 1440")
        runTest("SATB + ARG + GRBIA = GPSSS","1432 + 475 + 57204 = 59111")
        runTest("SY + SYL + AMA = KYQJ","82 + 825 + 363 = 1270")
        runTest("SATB + ARG + GRBIA = GPSSS","1432 + 475 + 57204 = 59111")
        runTest("SY + SYL + AMA = KYQJ","82 + 825 + 363 = 1270")
        runTest("CETC + ECV + OECXW = PLXVX","7647 + 672 + 86701 = 95020")
        runTest("PG + LGP + LGCC = PLYP","39 + 293 + 2911 = 3243")
        runTest("CETC + ECV + OECXW = PLXVX","7647 + 672 + 86701 = 95020")
        runTest("PG + LGP + LGCC = PLYP","39 + 293 + 2911 = 3243")
        runTest("ZLQOE + RO + LZJZJL + LJLMJLT = LELQEGZ","52793 + 69 + 250502 + 2024021 = 2327385")
        runTest("IRF + HIUF + RBFK + IUHRIUA = IUKKAAG","157 + 3197 + 5674 + 1935192 = 1944220")
        runTest("ZLQOE + RO + LZJZJL + LJLMJLT = LELQEGZ","52793 + 69 + 250502 + 2024021 = 2327385")
        runTest("IRF + HIUF + RBFK + IUHRIUA = IUKKAAG","157 + 3197 + 5674 + 1935192 = 1944220")
        runTest("QQB + NNIEK + RNI + TEVBQVQ = TERENEK","339 + 11820 + 618 + 5249343 = 5262120")
        runTest("FWH + NFHVV + YVVL + MUIFUN = MYVIFH","310 + 53099 + 8996 + 427325 = 489730")
        runTest("QQB + NNIEK + RNI + TEVBQVQ = TERENEK","339 + 11820 + 618 + 5249343 = 5262120")
        runTest("FWH + NFHVV + YVVL + MUIFUN = MYVIFH","310 + 53099 + 8996 + 427325 = 489730")
        runTest("GOU + SGU + GNON + NXHLGNN = NXXLGOX","850 + 480 + 8656 + 6213866 = 6223852")
        runTest("FWH + NFHVV + YVVL + MUIFUN = MYVIFH","310 + 53099 + 8996 + 427325 = 489730")
        runTest("GOU + SGU + GNON + NXHLGNN = NXXLGOX","850 + 480 + 8656 + 6213866 = 6223852")
        runTest("FWH + NFHVV + YVVL + MUIFUN = MYVIFH","310 + 53099 + 8996 + 427325 = 489730")
        runTest("GOU + SGU + GNON + NXHLGNN = NXXLGOX","850 + 480 + 8656 + 6213866 = 6223852")
        runTest("LJ + KNHL + IIHKJ + VIJO = HJNMV","78 + 9147 + 33498 + 5382 = 48105")
        runTest("RG + MGIGQ + MMHM + CSMRQVH = CRSQCZI","12 + 52329 + 5575 + 8051947 = 8109863")
        runTest("CS + XALA + HICN + ZAHSA = NHHXL","78 + 4505 + 6372 + 15685 = 26640")
        runTest("RG + MGIGQ + MMHM + CSMRQVH = CRSQCZI","12 + 52329 + 5575 + 8051947 = 8109863")
        runTest("CS + XALA + HICN + ZAHSA = NHHXL","78 + 4505 + 6372 + 15685 = 26640")
        runTest("KJCSE + JN + ENENE + KNSN = WSVYEJ","64379 + 48 + 98989 + 6878 = 170294")
        runTest("ZRMMR + WMOWM + LSMWZLS + WBSWMU = OVBUOWW","90440 + 64264 + 1546915 + 685647 = 2387266")
        runTest("OFWV + DYY + WFYYIV + DDFCIWC = FCWVFFF","5497 + 388 + 948867 + 3342692 = 4297444")
        runTest("ZRMMR + WMOWM + LSMWZLS + WBSWMU = OVBUOWW","90440 + 64264 + 1546915 + 685647 = 2387266")
        runTest("OFWV + DYY + WFYYIV + DDFCIWC = FCWVFFF","5497 + 388 + 948867 + 3342692 = 4297444")
        runTest("RGUGUB + SIRA + SABX + PXGAG = IPCPCA","681813 + 5760 + 5039 + 49808 = 742420")
        runTest("INMA + GCINA + AMMDLEE + DGYYMDA = MDNAAYY","7683 + 59763 + 3884022 + 4511843 = 8463311")
        runTest("VGFX + FPMGK + GPF + FPMXP = RQQXKP","7465 + 62849 + 426 + 62852 = 133592")
        runTest("BWWWW + ZMBZL + ZRIMRZ + IUZRIMI = BAPZLUU","30000 + 65361 + 682586 + 2968252 = 3746199")
        runTest("KGQG + ELL + QGS + QMQFL = ELEQSK","2898 + 100 + 984 + 97960 = 101942")
        runTest("BWWWW + ZMBZL + ZRIMRZ + IUZRIMI = BAPZLUU","30000 + 65361 + 682586 + 2968252 = 3746199")
        runTest("KGQG + ELL + QGS + QMQFL = ELEQSK","2898 + 100 + 984 + 97960 = 101942")
        runTest("CHKW + RHR + JNYNW + WYRY = WERJJ","5092 + 303 + 18482 + 2434 = 26311")
        runTest("KIHIOW + PDD + EJEJDRW + KHRKI = ROEHEWH","450539 + 677 + 1818729 + 40245 = 2310190")
        runTest("CJR + TCHR + TWK + TJWQ + HQTHKH = HQQKKS","749 + 2719 + 263 + 2468 + 182131 = 188330")
        runTest("LPLZ + LRL + PWG + BGJJF + BWBJU = FPBGU","8683 + 898 + 614 + 24775 + 21270 = 56240")
        runTest("CJR + TCHR + TWK + TJWQ + HQTHKH = HQQKKS","749 + 2719 + 263 + 2468 + 182131 = 188330")
        runTest("LPLZ + LRL + PWG + BGJJF + BWBJU = FPBGU","8683 + 898 + 614 + 24775 + 21270 = 56240")
        runTest("KPAA + TPYDYGR + RYPOK + OTARTDT + OKRK = GXYAXOG","4566 + 2501093 + 30574 + 7263212 + 7434 = 9806879")
        runTest("QHQO + LNKBBDD + DTK + NQDTK + LBBTQANK = BKKDBONQL","7874 + 9301155 + 560 + 37560 + 91167230 = 100514379")
        runTest("DYRVYQ + DSVDYOY + YORT + YDRY + DYRMSS = QIYDVRT","427625 + 4164292 + 2978 + 2472 + 427311 = 5024678")
        runTest("QHQO + LNKBBDD + DTK + NQDTK + LBBTQANK = BKKDBONQL","7874 + 9301155 + 560 + 37560 + 91167230 = 100514379")
        runTest("DYRVYQ + DSVDYOY + YORT + YDRY + DYRMSS = QIYDVRT","427625 + 4164292 + 2978 + 2472 + 427311 = 5024678")
        runTest("ATGYGT + GTU + XYWGYII + YUTUWYAA + GWYY = EAYEIWTE","179397 + 978 + 2369355 + 38786311 + 9633 = 41345674")
        runTest("KAKZ + ZDU + HDKKH + KHUHPZLL + KWADUKR = KAPZPWRA","1617 + 738 + 53115 + 15859744 + 1063812 = 16979026")
        runTest("JVRZVJ + VIZLZR + WRVZRZQ + WZQREVJ + IJEQ = ZWIQVLLJ","327123 + 241517 + 9721710 + 9107823 + 4380 = 19402553")
        runTest("DK + YKYWWY + KWJ + HJYH + VVYJJ = YJHVVZ","82 + 727557 + 254 + 3473 + 11744 = 743110")
        runTest("JVRZVJ + VIZLZR + WRVZRZQ + WZQREVJ + IJEQ = ZWIQVLLJ","327123 + 241517 + 9721710 + 9107823 + 4380 = 19402553")
        runTest("DK + YKYWWY + KWJ + HJYH + VVYJJ = YJHVVZ","82 + 727557 + 254 + 3473 + 11744 = 743110")
        runTest("XWLB + VZP + LPXIPX + IZZIWPFP + LLOW = IZLLIFFI","4691 + 302 + 924524 + 50056272 + 9986 = 50995775")
        runTest("HVHT + VGH + HTWVUW + HKCGCV + KUQWWTVW = KHHXXWXU","7475 + 417 + 750460 + 729194 + 26300540 = 27788086")
        runTest("GUQC + VUGQ + XCQX + PWGQ + UPPWVXV = UCQXQGU","3709 + 2730 + 6906 + 8430 + 7884262 = 7906037")
        runTest("EYK + CKBKOC + EYKXXY + KWICCIB + IXBX = BFEYOOK","934 + 245462 + 934003 + 4712215 + 1050 = 5893664")
        runTest("YYMTYX + YYL + CLYMYLL + MHXMRMNL + AHTAXRMH = NXRMRLHTR","778975 + 774 + 2478744 + 83580814 + 63965083 = 150804390")
        runTest("XLELXD + IIKL + QKELNUL + UINNQQ + NIOXXDKD = EODNKDLU","198912 + 6649 + 3489759 + 567733 + 76011242 = 80274295")
        runTest("UCRUPX + CBP + UQUP + UWQUCR + QFCQR = MQQXBBC","769708 + 630 + 7570 + 725769 + 54659 = 1558336")
        runTest("XLELXD + IIKL + QKELNUL + UINNQQ + NIOXXDKD = EODNKDLU","198912 + 6649 + 3489759 + 567733 + 76011242 = 80274295")
        runTest("UCRUPX + CBP + UQUP + UWQUCR + QFCQR = MQQXBBC","769708 + 630 + 7570 + 725769 + 54659 = 1558336")
        runTest("GVCYGG + KINY + NVGGKIG + YOGCV + KJIK = NHHCKNO","410944 + 5729 + 2144574 + 93401 + 5875 = 2660523")
        runTest("FGZK + GKGLKK + OGNOGKC + NOFKJKFF + JKLOJF = GLDLJNNZ","2741 + 717011 + 9759718 + 59216122 + 610962 = 70306554")
        runTest("XXQTMA + BTUJ + MHXM + UTBTHXAU + JBHTAO + HTJMMUJ = UXOBTBBX","996035 + 2048 + 3793 + 40207954 + 827051 + 7083348 = 49120229")
        runTest("NGNSP + AAPIU + GULN + PSGA + ULILAU + PZLHSAU = SPULNPL","39321 + 77185 + 9563 + 1297 + 568675 + 1460275 = 2156316")
        runTest("XXQTMA + BTUJ + MHXM + UTBTHXAU + JBHTAO + HTJMMUJ = UXOBTBBX","996035 + 2048 + 3793 + 40207954 + 827051 + 7083348 = 49120229")
        runTest("NGNSP + AAPIU + GULN + PSGA + ULILAU + PZLHSAU = SPULNPL","39321 + 77185 + 9563 + 1297 + 568675 + 1460275 = 2156316")
        runTest("KRU + XUXCNC + XKCV + LXBKLGCU + LRNVKKVN + KNKUKCCC = NGRGBKBV","154 + 747363 + 7138 + 27012934 + 25681186 + 16141333 = 69590108")
        runTest("JIV + NNLPIJ + JNNWVIA + PIRLHIJJ + AJWJIN + HJJVI = PNLVNILA","210 + 557412 + 2553016 + 41879122 + 623215 + 92201 = 45705176")
        runTest("ASSRG + GGNRW + TITW + NRTTRGWV + NGAW + IGATTAN = NURTTVSA","17706 + 66208 + 3438 + 20330689 + 2618 + 4613312 = 25033971")
        runTest("GLGNNK + JALJRJN + RGHHW + RJRQNKG + QJJNK + LANJWLRN = JLRNHALR","969228 + 7367472 + 49005 + 4741289 + 17728 + 63275642 = 76420364")
        runTest("VCVF + CVFVWEW + VHQCCJV + FQHJNHN + TJQJJCCH + CVHHE = FTEHCQCC","1918 + 9181252 + 1349971 + 8437030 + 67477993 + 91335 = 86539499")
        runTest("GLGNNK + JALJRJN + RGHHW + RJRQNKG + QJJNK + LANJWLRN = JLRNHALR","969228 + 7367472 + 49005 + 4741289 + 17728 + 63275642 = 76420364")
        runTest("VCVF + CVFVWEW + VHQCCJV + FQHJNHN + TJQJJCCH + CVHHE = FTEHCQCC","1918 + 9181252 + 1349971 + 8437030 + 67477993 + 91335 = 86539499")

        println("Running for ${System.currentTimeMillis() - startTime} msec")
    }

}