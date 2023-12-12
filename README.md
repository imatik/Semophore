# Semophore(1)

task:
Each interaction object is a thread
The problem of the bouquet makers. The three bouquet makers are represented by processes S1, S2 and S3. 
The three vendors are represented by processes V1, V2 and V3. Every bouquet maker needs roses, violets and peonies. 
When he has these resources, he spends some time making a bouquet and goes into a state of readiness to make a bouquet again. 
S1 has peonies in unlimited quantity, S2 has violets in unlimited quantity, S3 has roses in unlimited quantity. 
V1 supplies roses and violets, V2 supplies peonies and roses, V3 supplies peonies and violets. Mutual exclusion is provided for V1, V2 and V3. 
The next supplier cannot function until the resources of the previous supplier are consumed by the bouquet maker.

Задание:
Каждый объект взаимодействия - нить
Проблема составителей букетов. Три составителя букетов представлены процессами S1, S2 и S3. Три поставщика представлены процессами V1, V2 и V3.
Каждому составителю букетов необходимы розы, фиалки и пионы. Когда эти ресурсы у него есть, он некоторое время тратит на составление букета и переходит в состояние готовности составить букет снова. 
У S1 есть пионы в неограниченном количестве, у S2 есть фиалки в неограниченном количестве, у S3 есть розы в неограниченном количестве. V1 поставляет розы и фиалки, V2 поставляет пионы и розы, V3 поставляет пионы и фиалки.
Для V1, V2 и V3 обеспечивается взаимное исключение. 
Следующий поставщик не может функционировать, пока ресурсы предыдущего поставщика не будут потреблены составителем букетов.
