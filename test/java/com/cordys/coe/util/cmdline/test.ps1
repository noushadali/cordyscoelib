Param(
   [parameter(Mandatory=$true)]
   [alias("i")]
   $InputFile,
       [parameter(Mandatory=$true)]
   [alias("o")]
   $OutputFile)

echo "Blaaa" + $OutputFile
exit 0