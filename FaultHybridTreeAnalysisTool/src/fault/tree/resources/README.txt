The Fault tree model considered in this tool has the following representation as an XML:
(example tree is taken from the slide 9 of Part 3: Introducing the Programming Project)

<?xml version='1.0' encoding='UTF-8'?>
<faultTree type="hazard">
	<gate id="1" level="1" operation="OR">
		<gate id="2" level="2" operation="AND">
			<basic name="A" id="3" level="3" probability="0.2" />
			<basic name="B" id="4" level="3" probability="0.2" />
		</gate>
		<basic name="C" id="5" level="2" probability="0.1" />
	</gate>
</faultTree>