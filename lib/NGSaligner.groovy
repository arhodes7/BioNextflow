/* 
 * Class for NGS mappers 
 *
 * @authors
 * Luca Cozzuto <lucacozzuto@gmail.com>
 */
 
 class NGSaligner {

	/*
	 * Properties definition
	 */
	
     String aligner = ''
     String reference_file = ''
     String annotation_file = ''
     String id = ''
     String index = ''
     String lib_type = ''
     String reads = ''
     Integer read_size = 0
     String read1 = ''
     String read2 = ''
     String output = ''
     String result = ''
     Integer cpus = 1
     Integer memory = 2
     String extrapars = ''
	
	/*
	 * Methods definition
     */
     
    def public test() {
        output = this.dump()
    """
     echo '${output}'
    """
    }

    def public doAlignment(String aligner) { 
     switch (aligner) {
        case "STAR":
			this.alignWithStar()
            break
        default:
            break
    	}	
	}
	
    def public doIndexing(String aligner) { 
     switch (aligner) {
        case "STAR":
			this.indexWithSTAR()
            break
        default:
            break
    	}	
	}

	/*
     * Mapping SE and PE reads with STAR. Reads can be both gzipped and plain fastq
     */ 
	def private alignWithStar(){
        """
   		if [ `echo "${this.reads}"| cut -f 1 -d " " | grep ".gz"` ]; then gzipped=" --readFilesCommand zcat "; else gzipped=""; fi
				STAR --genomeDir ${this.index} \
						 --readFilesIn ${this.reads} \
						 \$gzipped \
						 --outSAMunmapped None \
						 --outSAMtype BAM SortedByCoordinate \
						 --runThreadN ${this.cpus} \
						 --quantMode GeneCounts \
						 --outFileNamePrefix ${this.id} \
						${this.extrapars}

			 
					mkdir ${output}
					mv ${this.id}Aligned* ${this.output}/.
					mv ${this.id}SJ* ${this.output}/.
					mv ${this.id}ReadsPerGene* ${this.output}/.
					mv ${this.id}Log* ${this.output}/. 		
		"""		
	}

      /*
       * Indexing a genome with STAR mapper. It reads both gzipped and plain fasta
       */ 
	
    def private indexWithSTAR() {
        """
		mkdir ${this.index}
     	if [ `echo ${this.reference_file} | grep ".gz"` ]; then 
			zcat ${this.reference_file} > `basename ${this.reference_file} .gz`
			STAR --runMode genomeGenerate --genomeDir ${this.index} --runThreadN ${this.cpus} \
			--genomeFastaFiles `basename ${this.reference_file} .gz` --sjdbGTFfile ${this.annotation_file} \
			--sjdbOverhang ${this.read_size} --outFileNamePrefix ${this.index} \
			${this.extrapars};
			rm `basename ${this.reference_file} .gz`
		else 
			STAR --runMode genomeGenerate --genomeDir ${this.index} --runThreadN ${this.cpus} \
			--genomeFastaFiles ${this.reference_file} --sjdbGTFfile ${this.annotation_file} \
			--sjdbOverhang ${this.read_size} --outFileNamePrefix ${this.index} \
			${this.extrapars}
		fi
		"""
    }



}
