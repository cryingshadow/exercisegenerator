DSALExercises:
	@touch a.class
	@rm *.class
ifeq ($(wildcard META-INF/MANIFEST.MF),) 
	@echo "Creating Manifest."
	@mkdir META-INF
	@touch META-INF/MANIFEST.MF
	@echo "Main-Class: DSALExercises" >> META-INF/MANIFEST.MF
else 
	@echo "Found Manifest."
endif
	@javac DSALExercises.java
	@jar cvmf META-INF/MANIFEST.MF DSALExercises.jar *.class
	@mv DSALExercises.jar ../style/
	@echo "Finished."