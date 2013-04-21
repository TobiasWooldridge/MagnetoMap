<?php

namespace SpaceApps\MagnetBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Template;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

use SpaceApps\MagnetBundle\Entity\Sample;


class ReadingController extends Controller
{
    /**
     * @Route("/")
     * @Template()
     */
    public function indexAction()
    {
	    $logger = $this->get('logger');
    	return array();
    }

    /**
	 * @Route("/readings")
     */
    public function recentAction()
    {
        $em = $this->get('doctrine')->getManager();

        $sampleResult = $em
                    ->createQuery('SELECT s.latitude, s.longitude, s.reading, s.time FROM SpaceAppsMagnetBundle:Sample s')
                    ->getResult();

        $samples = array();

        foreach ($sampleResult as $sample) {
        	if ($sample['reading'] >= 25 && $sample['reading'] <= 65) {
	            $samples[] = (array)$sample;
	        }
        }

        $response = new Response();
        $response->headers->set('Content-Type', 'application/json');
        $response->setContent(json_encode(array('samples' => $samples)));
    	return $response;
    }

    /**
	 * @Route("/readings/new")
     */
    public function newAction()
    {
	    $logger = $this->get('logger');
	    $logger->info("Received JSON");
	    
    	$samplesJson = $this->getRequest()->get('samples');

    	$newSample = json_decode($samplesJson, true);


		$logger->info(print_r($newSample, true));



		$sample = new Sample(); 

		$sample->setLongitude($newSample['long']);
		$sample->setLatitude($newSample['lat']);
		$sample->setTime(new \DateTime(date('c', $newSample['time']/1000)));
		$sample->setReading($newSample['mag']);


        $em = $this->get('doctrine')->getManager();
		$em->persist($sample);
		$em->flush();

		$logger->info("Constructed sample");

		$logger->info(print_r($sample, true));

    	return new Response();
    }

    /**
	 * @Route("/readings/metrics")
     */
    public function metricsAction()
    {
        $em = $this->get('doctrine')->getManager();

        $sampleResult = $em
                    ->createQuery('SELECT avg(s.reading) FROM SpaceAppsMagnetBundle:Sample s')
                    ->execute();

        return new Response(json_encode(array("average" => $sampleResult[0][1])));
	}

}
